package com.education.takeit.kafka.recommend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.education.takeit.admin.failLog.dto.DailyLogCountDto;
import com.education.takeit.admin.failLog.dto.RecomFailLogDto;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.kafka.recommend.dto.RecomFailDto;
import com.education.takeit.kafka.recommend.dto.RecomRequestDto;
import com.education.takeit.kafka.recommend.entity.RecomFailLog;
import com.education.takeit.kafka.recommend.producer.RecomKafkaProducer;
import com.education.takeit.kafka.recommend.repository.RecomFailRepository;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class RecomFailLogServiceTest {

  @Mock private RecomFailRepository recomFailRepository;

  @Mock private UserRepository userRepository;

  @Mock private RecomKafkaProducer producer;

  @InjectMocks private RecomFailLogService service;

  @Test
  void saveFailLog_정상저장() {
    // given
    RecomFailDto dto = new RecomFailDto(1L, 2L, "CODE", "message");
    User mockUser = User.builder().build();

    when(userRepository.getReferenceById(1L)).thenReturn(mockUser);

    // when
    service.saveFailLog(dto);

    // then
    verify(recomFailRepository, times(1)).save(any(RecomFailLog.class));
  }

  @Test
  void getPendingFailLogs_정상조회() {
    // given
    User user = User.builder().email("test@example.com").nickname("nickname").build();
    RecomFailLog log =
        RecomFailLog.builder()
            .id(1L)
            .user(user)
            .subjectId(2L)
            .errorCode("CODE")
            .errorMessage("msg")
            .retry(false)
            .createdDt(LocalDateTime.now())
            .build();

    Pageable pageable = PageRequest.of(0, 10);
    Page<RecomFailLog> page = new PageImpl<>(List.of(log));

    when(recomFailRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

    // when
    Page<RecomFailLogDto> result =
        service.getPendingFailLogs("nickname", "test@example.com", "CODE", pageable);

    // then
    assertThat(result.getTotalElements()).isEqualTo(1);
    verify(recomFailRepository).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  void getDailyCounts_정상조회() {
    // given
    LocalDateTime now = LocalDateTime.now();
    List<RecomFailLog> logs =
        List.of(
            RecomFailLog.builder().createdDt(now.minusDays(1)).build(),
            RecomFailLog.builder().createdDt(now.minusDays(2)).build(),
            RecomFailLog.builder().createdDt(now.minusDays(2)).build());

    when(recomFailRepository.findByCreatedDtBetween(any(), any())).thenReturn(logs);

    // when
    List<DailyLogCountDto> result = service.getDailyCounts();

    // then
    assertThat(result).hasSize(7);
    long totalCount = result.stream().mapToLong(DailyLogCountDto::count).sum();
    assertThat(totalCount).isEqualTo(3L);
  }

  @Test
  void retryFailLog_존재하지_않을_경우_예외() {
    // given
    when(recomFailRepository.findById(1L)).thenReturn(Optional.empty());

    // then
    assertThatThrownBy(() -> service.retryFailLog(1L))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(StatusCode.NOT_EXIST_LOG.getMessage());
  }

  @Test
  void retryFailLog_정상_처리() {
    // given
    User user = User.builder().build();
    RecomFailLog log = RecomFailLog.builder().id(1L).user(user).subjectId(2L).retry(false).build();

    when(recomFailRepository.findById(1L)).thenReturn(Optional.of(log));
    when(recomFailRepository.save(any())).thenReturn(log);

    // when
    service.retryFailLog(1L);

    // then
    verify(recomFailRepository, times(1)).save(any(RecomFailLog.class));
    verify(producer, times(1)).publish(any(RecomRequestDto.class));
    assertThat(log.getRetry()).isTrue();
  }
}

package com.education.takeit.kafka.feedback.service;

import com.education.takeit.admin.failLog.dto.DailyLogCountDto;
import com.education.takeit.admin.failLog.dto.FeedbackFailLogDto;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.kafka.feedback.dto.FeedbackFailDto;
import com.education.takeit.kafka.feedback.dto.FeedbackRequestDto;
import com.education.takeit.kafka.feedback.entity.FeedbackFailLog;
import com.education.takeit.kafka.feedback.producer.FeedbackKafkaProducer;
import com.education.takeit.kafka.feedback.repository.FeedbackFailRepository;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackFailLogServiceTest {

    @Mock
    private FeedbackFailRepository feedbackFailRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FeedbackKafkaProducer producer;

    @InjectMocks
    private FeedbackFailLogService service;

    @Test
    void saveFailLog_정상저장() {
        //given
        FeedbackFailDto dto = new FeedbackFailDto(1L, 2L, "pre", 0, "ERROR", "message");
        User mockUser = User.builder().build();

        when(userRepository.getReferenceById(1L)).thenReturn(mockUser);
        //when
        service.saveFailLog(dto);

        //then
        verify(feedbackFailRepository, times(1)).save(any(FeedbackFailLog.class));
    }

    @Test
    void getPendingFailLogs_정상조회() {
        // given
        User user = User.builder()
                .email("test@example.com")
                .nickname("nickname")
                .build();
        FeedbackFailLog log = FeedbackFailLog.builder()
                .id(1L)
                .user(user)
                .subjectId(2L)
                .type("pre")
                .nth(1)
                .errorCode("CODE")
                .errorMessage("msg")
                .retry(false)
                .createdDt(LocalDateTime.now())
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<FeedbackFailLog> page = new PageImpl<>(List.of(log));

        when(feedbackFailRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        // when
        Page<FeedbackFailLogDto> result = service.getPendingFailLogs("nickname", "test@example.com", "CODE", pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(feedbackFailRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getDailyCounts_정상조회() {
        // given
        LocalDateTime now = LocalDateTime.now();
        List<FeedbackFailLog> logs = List.of(
                FeedbackFailLog.builder().createdDt(now.minusDays(1)).build(),
                FeedbackFailLog.builder().createdDt(now.minusDays(2)).build(),
                FeedbackFailLog.builder().createdDt(now.minusDays(2)).build()
        );

        when(feedbackFailRepository.findByCreatedDtBetween(any(), any())).thenReturn(logs);

        // when
        List<DailyLogCountDto> result = service.getDailyCounts();

        // then
        assertThat(result).hasSize(7); // 최근 7일
        long totalCount = result.stream().mapToLong(DailyLogCountDto::count).sum();
        assertThat(totalCount).isEqualTo(3L);
    }

    @Test
    void retryFailLog_존재하지_않을_경우_예외() {
        // given
        when(feedbackFailRepository.findById(1L)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> service.retryFailLog(1L))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(StatusCode.NOT_EXIST_LOG.getMessage());
    }

    @Test
    void retryFailLog_정상_처리() {
        // given
        User user = User.builder().build();
        FeedbackFailLog log = FeedbackFailLog.builder()
                .id(1L)
                .user(user)
                .subjectId(2L)
                .type("pre")
                .nth(3)
                .retry(false)
                .build();

        when(feedbackFailRepository.findById(1L)).thenReturn(Optional.of(log));
        when(feedbackFailRepository.save(any())).thenReturn(log);

        // when
        service.retryFailLog(1L);

        // then
        verify(feedbackFailRepository, times(1)).save(any(FeedbackFailLog.class));
        verify(producer, times(1)).publish(any(FeedbackRequestDto.class));
        assertThat(log.getRetry()).isTrue();
    }
}
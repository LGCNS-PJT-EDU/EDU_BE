package com.education.takeit.kafka.recommand.service;

import com.education.takeit.admin.failLog.dto.DailyLogCountDto;
import com.education.takeit.admin.failLog.dto.RecomFailLogDto;
import com.education.takeit.admin.failLog.spec.FailLogSpecifications;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.kafka.recommand.dto.RecomFailDto;
import com.education.takeit.kafka.recommand.dto.RecomRequestDto;
import com.education.takeit.kafka.recommand.entity.RecomFailLog;
import com.education.takeit.kafka.recommand.producer.RecomKafkaProducer;
import com.education.takeit.kafka.recommand.repository.RecomFailRepository;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecomFailLogService {

  private final RecomFailRepository recomFailRepository;
  private final UserRepository userRepository;
  private final RecomKafkaProducer producer;

  /**
   * 추천 컨텐츠 생성 실패 로그 적재
   *
   * @param dto
   */
  @Transactional
  public void saveFailLog(RecomFailDto dto) {
    User user = userRepository.getReferenceById(dto.userId());
    RecomFailLog entity =
        RecomFailLog.builder()
            .user(user)
            .subjectId(dto.subjectId())
            .errorCode(dto.errorCode())
            .errorMessage(dto.errorMessage())
            .retry(false)
            .build();

    recomFailRepository.save(entity);
  }

  @Transactional(readOnly = true)
  public Page<RecomFailLogDto> getPendingFailLogs(
      String nickname, String email, String errorCode, Pageable pageable) {
    Specification<RecomFailLog> spec = FailLogSpecifications.searchBy(nickname, email, errorCode);

    return recomFailRepository
        .findAll(spec, pageable)
        .map(
            r ->
                new RecomFailLogDto(
                    r.getId(),
                    r.getUser().getEmail(),
                    r.getUser().getNickname(),
                    r.getSubjectId(),
                    r.getErrorCode(),
                    r.getErrorMessage(),
                    r.getRetry(),
                    r.getCreatedDt()));
  }

  @Transactional(readOnly = true)
  public List<DailyLogCountDto> getDailyCounts() {
    LocalDate today = LocalDate.now();
    LocalDateTime start = today.minusDays(6).atStartOfDay();
    LocalDateTime end = today.plusDays(1).atStartOfDay();

    List<RecomFailLog> logs = recomFailRepository.findByCreatedDtBetween(start, end);

    Map<LocalDate, Long> countsByDate =
        logs.stream()
            .collect(
                Collectors.groupingBy(
                    log -> log.getCreatedDt().toLocalDate(), Collectors.counting()));

    return start
        .toLocalDate()
        .datesUntil(end.toLocalDate())
        .map(date -> new DailyLogCountDto(date, countsByDate.getOrDefault(date, 0L)))
        .collect(Collectors.toList());
  }

  @Transactional
  public void retryFailLog(Long id) {
    RecomFailLog log =
        recomFailRepository
            .findById(id)
            .orElseThrow(() -> new CustomException(StatusCode.NOT_EXIST_LOG));

    // retry 플래그 업데이트
    log.markRetry();
    recomFailRepository.save(log);

    // 재발행
    RecomRequestDto req = new RecomRequestDto(log.getUser().getUserId(), log.getSubjectId());
    producer.publish(req);
  }
}

package com.education.takeit.kafka.feedback.service;

import com.education.takeit.admin.failLog.dto.DailyLogCountDto;
import com.education.takeit.admin.failLog.dto.FeedbackFailLogDto;
import com.education.takeit.admin.failLog.spec.FailLogSpecifications;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.kafka.feedback.dto.FeedbackFailDto;
import com.education.takeit.kafka.feedback.dto.FeedbackRequestDto;
import com.education.takeit.kafka.feedback.entity.FeedbackFailLog;
import com.education.takeit.kafka.feedback.producer.FeedbackKafkaProducer;
import com.education.takeit.kafka.feedback.repository.FeedbackFailRepository;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackFailLogService {

  private final FeedbackFailRepository feedbackFailRepository;
  private final UserRepository userRepository;
  private final FeedbackKafkaProducer producer;

  /**
   * 피드백 생성 실패 DB 로그 적재
   *
   * @param dto
   */
  @Transactional
  public void saveFailLog(FeedbackFailDto dto) {
    User user = userRepository.getReferenceById(dto.userId());

    FeedbackFailLog entity =
        FeedbackFailLog.builder()
            .user(user)
            .subjectId(dto.subjectId())
            .type(dto.type())
            .nth(dto.nth())
            .errorCode(dto.errorCode())
            .errorMessage(dto.errorMessage())
                .retry(false)
            .build();

    feedbackFailRepository.save(entity);
  }

  @Transactional(readOnly = true)
  public Page<FeedbackFailLogDto> getPendingFailLogs(
          String nickname,
          String email,
          String errorCode,
          Pageable pageable
  ) {
    Specification<FeedbackFailLog> spec =
            FailLogSpecifications.searchBy(nickname, email, errorCode);
    return feedbackFailRepository.findAll(spec, pageable)
            .map(f -> new FeedbackFailLogDto(
                    f.getId(),
                    f.getUser().getEmail(),
                    f.getUser().getNickname(),
                    f.getSubjectId(),
                    f.getType(),
                    f.getNth(),
                    f.getErrorCode(),
                    f.getErrorMessage(),
                    f.getRetry(),
                    f.getCreatedDt()
            ));
  }

  @Transactional(readOnly = true)
  public List<DailyLogCountDto> getDailyCounts() {
    LocalDate today      = LocalDate.now();
    LocalDateTime start  = today.minusDays(6).atStartOfDay();
    LocalDateTime end    = today.plusDays(1).atStartOfDay();

    List<FeedbackFailLog> logs = feedbackFailRepository.findByCreatedDtBetween(start, end);

    Map<LocalDate, Long> countsByDate = logs.stream()
            .collect(Collectors.groupingBy(
                    log -> log.getCreatedDt().toLocalDate(),
                    Collectors.counting()
            ));

    return start.toLocalDate()
            .datesUntil(end.toLocalDate())
            .map(date -> new DailyLogCountDto(
                    date,
                    countsByDate.getOrDefault(date, 0L)
            ))
            .collect(Collectors.toList());
  }

  @Transactional
  public void retryFailLog(Long id) {
    FeedbackFailLog log = feedbackFailRepository.findById(id)
            .orElseThrow(() -> new CustomException(
                    StatusCode.NOT_EXIST_LOG
            ));

    // retry 플래그 업데이트
    log.markRetry();
    feedbackFailRepository.save(log);

    // 재발행
    FeedbackRequestDto req = new FeedbackRequestDto(
            log.getUser().getUserId(),
            log.getSubjectId(),
            log.getType(),
            log.getNth()
    );
    producer.publish(req);
  }
}

package com.education.takeit.kafka.feedback.service;

import com.education.takeit.admin.failLog.dto.FeedbackFailLogDto;
import com.education.takeit.admin.failLog.spec.FailLogSpecifications;
import com.education.takeit.kafka.feedback.dto.FeedbackFailDto;
import com.education.takeit.kafka.feedback.entity.FeedbackFailLog;
import com.education.takeit.kafka.feedback.repository.FeedbackFailRepository;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedbackFailLogService {

  private final FeedbackFailRepository feedbackFailRepository;
  private final UserRepository userRepository;

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
}

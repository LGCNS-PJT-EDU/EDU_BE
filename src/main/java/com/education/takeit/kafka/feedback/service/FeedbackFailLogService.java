package com.education.takeit.kafka.feedback.service;

import com.education.takeit.kafka.feedback.dto.FeedbackFailDto;
import com.education.takeit.kafka.feedback.entity.FeedbackFailLog;
import com.education.takeit.kafka.feedback.repository.FeedbackFailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackFailLogService {

  private final FeedbackFailRepository repository;

  /**
   * 피드백 생성 실패 DB 로그 적재
   *
   * @param dto
   */
  public void saveFailLog(FeedbackFailDto dto) {
    FeedbackFailLog entity =
        FeedbackFailLog.builder()
            .userId(dto.userId())
            .subjectId(dto.subjectId())
            .type(dto.type())
            .nth(dto.nth())
            .errorCode(dto.errorCode())
            .errorMessage(dto.errorMessage())
            .build();

    repository.save(entity);
  }
}

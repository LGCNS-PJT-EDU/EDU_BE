package com.education.takeit.kafka.service;

import com.education.takeit.kafka.dto.FeedbackFailDto;
import com.education.takeit.kafka.entity.FeedbackFailLog;
import com.education.takeit.kafka.repository.FeedbackFailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackFailLogService {

  private final FeedbackFailRepository repository;

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

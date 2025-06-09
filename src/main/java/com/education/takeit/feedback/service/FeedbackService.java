package com.education.takeit.feedback.service;

import com.education.takeit.feedback.dto.FeedbackDto;
import com.education.takeit.feedback.dto.FeedbackResponseDto;
import com.education.takeit.feedback.entity.Feedback;
import com.education.takeit.feedback.repository.FeedbackRepository;
import com.education.takeit.global.client.AIClient;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.kafka.feedback.dto.FeedbackResultDto;
import com.education.takeit.roadmap.entity.Subject;
import com.education.takeit.roadmap.repository.SubjectRepository;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackService {

  private final AIClient aiClient;
  private final FeedbackRepository feedbackRepository;
  private final UserRepository userRepository;
  private final SubjectRepository subjectRepository;
  private final ObjectMapper objectMapper;

  public List<FeedbackResponseDto> findFeedback(Long userId) {
    return aiClient.getFeedback(userId);
  }

  @Transactional
  public void saveFeedback(FeedbackResultDto feedbackResultDto) {
    User user =
        userRepository
            .findByUserId(feedbackResultDto.userId())
            .orElseThrow(() -> new CustomException(StatusCode.NOT_EXIST_USER));
    Subject subject =
        subjectRepository
            .findBySubId(feedbackResultDto.subjectId())
            .orElseThrow(() -> new CustomException(StatusCode.NOT_EXIST_SUBJECT));

    FeedbackDto feedbackDto = feedbackResultDto.feedback().feedback();

    String strengthJson = toJson(feedbackDto.strength());
    String weaknessJson = toJson(feedbackDto.weakness());

    Feedback feedback =
        new Feedback(
            feedbackDto.finalComment(),
            feedbackResultDto.nth(),
            "pre".equalsIgnoreCase(feedbackResultDto.type()),
            user,
            subject,
            strengthJson,
            weaknessJson);

    feedbackRepository.save(feedback);
  }

  private String toJson(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("JSON 직렬화 실패.", e);
    }
  }
}

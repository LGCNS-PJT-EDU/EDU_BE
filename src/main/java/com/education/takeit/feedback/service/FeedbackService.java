package com.education.takeit.feedback.service;

import com.education.takeit.feedback.dto.FeedbackDto;
import com.education.takeit.feedback.dto.FeedbackFindResponseDto;
import com.education.takeit.feedback.entity.Feedback;
import com.education.takeit.feedback.repository.FeedbackRepository;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.kafka.feedback.dto.FeedbackResultDto;
import com.education.takeit.roadmap.entity.Subject;
import com.education.takeit.roadmap.repository.SubjectRepository;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackService {

  private final FeedbackRepository feedbackRepository;
  private final UserRepository userRepository;
  private final SubjectRepository subjectRepository;
  private final ObjectMapper objectMapper;

  public List<FeedbackFindResponseDto> findFeedback(Long userId, Long subjectId) {
    List<Feedback> feedbacks =
        feedbackRepository.findByUser_UserIdAndSubject_SubId(userId, subjectId);

    return feedbacks.stream()
        .map(
            feedback -> {
              Long feedbackUserId = feedback.getUser().getUserId();
              LocalDateTime createdAt = feedback.getCreatedAt();
              String subNm = feedback.getSubject().getSubNm();

              Map<String, Integer> scoreMap = parseScoresJson(feedback.getScores());

              Map<String, String> strengthMap = parseJson(feedback.getStrength());
              Map<String, String> weaknessMap = parseJson(feedback.getWeakness());

              FeedbackDto feedbackDto =
                  new FeedbackDto(strengthMap, weaknessMap, feedback.getFeedbackContent());

              return new FeedbackFindResponseDto(
                  feedbackUserId, createdAt, subNm, scoreMap, feedbackDto);
            })
        .toList();
  }

  @Transactional
  public void saveFeedback(FeedbackResultDto feedbackResultDto) {
    User user =
        userRepository
            .findByUserId(feedbackResultDto.userId())
            .orElseThrow(() -> new CustomException(StatusCode.USER_NOT_FOUND));
    Subject subject =
        subjectRepository
            .findBySubId(feedbackResultDto.subjectId())
            .orElseThrow(() -> new CustomException(StatusCode.NOT_EXIST_SUBJECT));

    FeedbackDto feedbackDto = feedbackResultDto.feedback().feedback();
    Map<String, Integer> scores = feedbackResultDto.feedback().scores();
    String scoreJson = toJson(scores);
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
            weaknessJson,
            scoreJson);

    feedbackRepository.save(feedback);
  }

  private String toJson(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("JSON 직렬화 실패.", e);
    }
  }

  private Map<String, Integer> parseScoresJson(String json) {
    try {
      return objectMapper.readValue(json, new TypeReference<>() {});
    } catch (JsonProcessingException e) {
      throw new RuntimeException("JSON 역직렬화 실패", e);
    }
  }

  private Map<String, String> parseJson(String json) {
    try {
      return objectMapper.readValue(json, new TypeReference<>() {});
    } catch (JsonProcessingException e) {
      throw new RuntimeException("JSON 역직렬화 실패", e);
    }
  }
}

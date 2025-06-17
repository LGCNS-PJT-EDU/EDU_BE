package com.education.takeit.feedback.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.BDDMockito.*;

import com.education.takeit.feedback.dto.FeedbackDto;
import com.education.takeit.feedback.dto.FeedbackFindResponseDto;
import com.education.takeit.feedback.dto.FeedbackResponseDto;
import com.education.takeit.feedback.dto.InfoDto;
import com.education.takeit.feedback.entity.Feedback;
import com.education.takeit.feedback.repository.FeedbackRepository;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.kafka.feedback.dto.FeedbackResultDto;
import com.education.takeit.roadmap.entity.Subject;
import com.education.takeit.roadmap.entity.Track;
import com.education.takeit.roadmap.repository.SubjectRepository;
import com.education.takeit.user.entity.LoginType;
import com.education.takeit.user.entity.Role;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {
  @Mock private FeedbackRepository feedbackRepository;
  @Mock private UserRepository userRepository;
  @Mock private SubjectRepository subjectRepository;
  @Mock private ObjectMapper objectMapper;

  @InjectMocks private FeedbackService feedbackService;

  private final Track track = new Track();
  private final User dummyUser =
      new User("test@test.com", "testUser", "test", LoginType.LOCAL, Role.USER, true);
  private final Subject dummySubject =
      Subject.builder()
          .subNm("Java")
          .subId(1L)
          .track(track)
          .baseSubOrder(1)
          .subEssential("Y")
          .subType("BE")
          .subOverview("")
          .build();

  // 기능 테스트를 위해 간소화한 데이터를 만든다
  private FeedbackResponseDto createDummyDto(Long userId, String subject) {
    return new FeedbackResponseDto(
        new InfoDto(userId, LocalDate.of(2025, 5, 9), subject),
        Map.of("total", 42),
        new FeedbackDto(Map.of("a", "A"), Map.of("b", "B"), "final"));
  }

  private FeedbackResultDto createSaveDto() {
    return new FeedbackResultDto(1L, 1L, "pre", 1, createDummyDto(1L, "Java"));
  }

  @Test
  @DisplayName("정상적으로 피드백을 저장한다")
  void 피드백_저장_성공() throws JsonProcessingException {
    // Given
    FeedbackResultDto dto = createSaveDto();

    given(userRepository.findByUserId(1L)).willReturn(Optional.of(dummyUser));
    given(subjectRepository.findBySubId(1L)).willReturn(Optional.of(dummySubject));
    given(objectMapper.writeValueAsString(any())).willReturn("{json}");

    // When
    feedbackService.saveFeedback(dto);

    // Then
    then(userRepository).should().findByUserId(1L);
    then(subjectRepository).should().findBySubId(1L);
    then(feedbackRepository)
        .should()
        .save(
            argThat(
                feedback ->
                    feedback.getFeedbackContent().equals("final")
                        && feedback.getUser().equals(dummyUser)
                        && feedback.getSubject().equals(dummySubject)
                        && feedback.getNth() == 1
                        && feedback.isPre()
                        && feedback.getStrength().equals("{json}")
                        && feedback.getWeakness().equals("{json}")));
  }

  @Test
  @DisplayName("과목이 없으면 예외가 발생한다")
  void 피드백_저장_과목_없음() {
    FeedbackResultDto dto = createSaveDto();
    given(userRepository.findByUserId(1L)).willReturn(Optional.of(dummyUser));
    given(subjectRepository.findBySubId(1L)).willReturn(Optional.empty());

    assertThatThrownBy(() -> feedbackService.saveFeedback(dto))
        .isInstanceOf(CustomException.class)
        .extracting("statusCode")
        .isEqualTo(StatusCode.NOT_EXIST_SUBJECT);
  }

  @Test
  @DisplayName("JSON 직렬화 실패 시 RuntimeException 발생")
  void 피드백_저장_JSON_직렬화_실패() throws JsonProcessingException {
    FeedbackResultDto dto = createSaveDto();
    given(userRepository.findByUserId(1L)).willReturn(Optional.of(dummyUser));
    given(subjectRepository.findBySubId(1L)).willReturn(Optional.of(dummySubject));
    given(objectMapper.writeValueAsString(any())).willThrow(JsonProcessingException.class);

    assertThatThrownBy(() -> feedbackService.saveFeedback(dto))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("JSON 직렬화 실패");
  }

  @Test
  @DisplayName("RDB에 저장된 피드백 조회 성공")
  void 피드백_조회_성공() throws JsonProcessingException {
    // Given
    Feedback feedback =
        new Feedback(
            "final",
            1,
            true,
            dummyUser,
            dummySubject,
            "{\"a\":\"A\"}",
            "{\"b\":\"B\"}",
            "{\"total\":42}");
    given(feedbackRepository.findByUser_UserIdAndSubject_SubId(1L, 1L))
        .willReturn(List.of(feedback));
    given(objectMapper.readValue(eq("{\"a\":\"A\"}"), any(TypeReference.class)))
        .willReturn(Map.of("a", "A"));
    given(objectMapper.readValue(eq("{\"b\":\"B\"}"), any(TypeReference.class)))
        .willReturn(Map.of("b", "B"));
    given(objectMapper.readValue(eq("{\"total\":42}"), any(TypeReference.class)))
        .willReturn(Map.of("total", 42));

    // When
    List<FeedbackFindResponseDto> result = feedbackService.findFeedback(1L, 1L);

    // Then
    assertThat(result).hasSize(1);
    FeedbackFindResponseDto dto = result.getFirst();
    assertThat(dto.subject()).isEqualTo("Java");
    assertThat(dto.scores()).containsEntry("total", 42);
    assertThat(dto.feedback().strength()).containsEntry("a", "A");
    assertThat(dto.feedback().weakness()).containsEntry("b", "B");
    assertThat(dto.feedback().finalComment()).isEqualTo("final");
  }
}

package com.education.takeit.feedback.service;

import com.education.takeit.feedback.dto.FeedbackDto;
import com.education.takeit.feedback.dto.FeedbackResponseDto;
import com.education.takeit.feedback.dto.InfoDto;
import com.education.takeit.global.client.AIClient;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

  @Mock private AIClient mockClient;

  @InjectMocks private FeedbackService feedbackService;

  // 기능 테스트를 위해 간소화한 데이터를 만든다
  private FeedbackResponseDto createDummyDto(String userId, String subject) {
    return new FeedbackResponseDto(
        new InfoDto(userId, LocalDate.of(2025, 5, 9), subject),
        Map.of("total", 42),
        new FeedbackDto(Map.of("a", "A"), Map.of("b", "B"), "final"));
  }

  @Test
  @DisplayName("정상 응답 시 데이터를 그대로 반환한다 - 데이터 여러 개")
  void testFindMultipleFeedback() {
    // 2개의 데이터(JSON 데이터)가 들어있는 리스트를 만든다
    var dummyList =
        List.of(createDummyDto("user1", "JavaScript"), createDummyDto("user1", "Python"));
    given(mockClient.getFeedback("user1")).willReturn(dummyList);

    // user1이 들어있는 데이터를 검색한다
    var result = feedbackService.findFeedback("user1");

    // mockClient가 한번 호출되었는가? -> 리스트 크기가 2인가? -> 각 DTO의 userId가 user1인가?
    then(mockClient).should().getFeedback("user1");
    assertThat(result)
        .hasSize(2)
        .extracting(r -> r.info().userId())
        .containsExactly("user1", "user1");
  }

  @Test
  @DisplayName("정상 응답 시 데이터를 그대로 반환한다 - 데이터 1개")
  void testFindSingleFeedback() {
    // 1개 데이터만 들어있는 리스트를 만든다
    var dummyList = List.of(createDummyDto("user1", "JavaScript"));
    given(mockClient.getFeedback("user1")).willReturn(dummyList);

    // user1이 들어있는 데이터를 검색한다
    var result = feedbackService.findFeedback("user1");

    // mockClient가 한번 호출되었는가? -> 리스트 크기가 1인가? -> DTO의 userId가 user1인가?
    then(mockClient).should().getFeedback("user1");
    assertThat(result).hasSize(1).extracting(r -> r.info().userId()).containsExactly("user1");
  }

  @Test
  @DisplayName("정상 응답 시 데이터를 그대로 반환한다 - 데이터가 없는 빈 배열 ")
  void testFindEmptyFeedback() {
    // 빈 리스트를 만든다
    given(mockClient.getFeedback("emptyUser")).willReturn(List.of());

    // 리스트를 검색한다
    var result = feedbackService.findFeedback("emptyUser");

    // mockClient가 한번 호출되었는가? -> 리스트가 빈 리스트인가?
    then(mockClient).should().getFeedback("emptyUser");
    assertThat(result).isNotNull().isEmpty();
  }

  @Test
  @DisplayName("502 에러는 서버 연결 자체가 실패했음을 의미한다")
  void test502Status() {
    // CONNECTION_FAILED 상황일 때
    given(mockClient.getFeedback(anyString()))
        .willThrow(new CustomException(StatusCode.CONNECTION_FAILED));

    // 동일 에러가 발생하는가?
    assertThatThrownBy(() -> feedbackService.findFeedback("userX"))
        .isInstanceOf(CustomException.class)
        .extracting("statusCode")
        .isEqualTo(StatusCode.CONNECTION_FAILED);
  }
}

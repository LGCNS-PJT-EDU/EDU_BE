package com.education.takeit.kafka.feedback.consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.education.takeit.feedback.dto.FeedbackDto;
import com.education.takeit.feedback.dto.FeedbackResponseDto;
import com.education.takeit.feedback.dto.InfoDto;
import com.education.takeit.feedback.service.FeedbackService;
import com.education.takeit.kafka.feedback.dto.FeedbackResultDto;
import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

@ExtendWith(MockitoExtension.class)
class FeedbackSuccessKafkaConsumerTest {

  @Mock private FeedbackService feedbackService;

  @Mock private Acknowledgment acknowledgment;

  @InjectMocks private FeedbackSuccessKafkaConsumer consumer;

  private FeedbackResultDto testPayload;

  @BeforeEach
  void setUp() {
    InfoDto info = new InfoDto(1L, LocalDate.now(), "HTML");
    Map<String, Integer> scores = Map.of("score1", 90, "score2", 85);
    Map<String, String> strength = Map.of("s1", "Good");
    Map<String, String> weakness = Map.of("w1", "Need");
    FeedbackDto feedback = new FeedbackDto(strength, weakness, "GOOD!");
    FeedbackResponseDto feedbackResponse = new FeedbackResponseDto(info, scores, feedback);

    testPayload = new FeedbackResultDto(1L, 1L, "pre", 1, feedbackResponse);
  }

  @Test
  @DisplayName(
      "성공적인 피드백 수신 시 FeedbackService.saveFeedback이 호출되고 Acknowledgment.acknowledge()가 호출되어야 한다")
  void 피드백_성공_이벤트_소비_성공() {
    // Given
    doNothing().when(feedbackService).saveFeedback(any(FeedbackResultDto.class));

    // When
    consumer.consumeSuccess(testPayload, acknowledgment);

    // Then
    verify(feedbackService, times(1)).saveFeedback(any(FeedbackResultDto.class));
    verify(acknowledgment, times(1)).acknowledge();
  }

  @Test
  @DisplayName(
      "피드백 저장 실패 시 FeedbackService.saveFeedback에서 예외가 발생하고 Acknowledgment.acknowledge()가 호출되지 않아야 한다")
  void 피드백_성공_이벤트_소비_실패() {
    // Given
    RuntimeException serviceException = new RuntimeException("피드백 저장 중 데이터베이스 오류 발생");
    doThrow(serviceException).when(feedbackService).saveFeedback(any(FeedbackResultDto.class));

    // When & Then
    RuntimeException thrownException =
        assertThrows(
            RuntimeException.class,
            () -> {
              consumer.consumeSuccess(testPayload, acknowledgment);
            });

    verify(feedbackService, times(1)).saveFeedback(any(FeedbackResultDto.class));
    verify(acknowledgment, never()).acknowledge();
    assertThat(thrownException.getCause()).isEqualTo(serviceException);
  }
}

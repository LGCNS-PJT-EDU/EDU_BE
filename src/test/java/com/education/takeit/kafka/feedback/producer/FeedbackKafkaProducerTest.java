package com.education.takeit.kafka.feedback.producer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.education.takeit.kafka.feedback.dto.FeedbackFailDto;
import com.education.takeit.kafka.feedback.dto.FeedbackRequestDto;
import java.util.concurrent.CompletableFuture;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

@ExtendWith(MockitoExtension.class)
class FeedbackKafkaProducerTest {

  @Mock private KafkaTemplate<String, FeedbackRequestDto> feedbackKafkaTemplate;

  @Mock private KafkaTemplate<String, FeedbackFailDto> feedbackFailKafkaTemplate;

  private FeedbackKafkaProducer producer;

  private static final String FEEDBACK_REQUEST_TOPIC = "feedback.request";
  private static final String FEEDBACK_FAIL_TOPIC = "feedback.result.fail";
  private FeedbackRequestDto testRequestDto;

  @BeforeEach
  void setUp() {
    testRequestDto = new FeedbackRequestDto(1L, 2L, "pre", 0);
    producer = new FeedbackKafkaProducer(feedbackKafkaTemplate, feedbackFailKafkaTemplate);
  }

  @Test
  @DisplayName("성공적인 피드백 요청 발행 시 KafkaTemplate.send가 호출되고 실패 토픽으로 전송되지 않아야 한다")
  void 정상_발행_성공() {
    // Given
    ProducerRecord<String, FeedbackRequestDto> producerRecord =
        new ProducerRecord<>(
            FEEDBACK_REQUEST_TOPIC, testRequestDto.userId().toString(), testRequestDto);
    RecordMetadata metadata =
        new RecordMetadata(
            new TopicPartition(FEEDBACK_REQUEST_TOPIC, 0), 0L, 0, System.currentTimeMillis(), 0, 0);
    SendResult<String, FeedbackRequestDto> sendResult = new SendResult<>(producerRecord, metadata);
    CompletableFuture<SendResult<String, FeedbackRequestDto>> successFuture =
        CompletableFuture.completedFuture(sendResult);

    when(feedbackKafkaTemplate.send(
            eq(FEEDBACK_REQUEST_TOPIC),
            eq(testRequestDto.userId().toString()),
            any(FeedbackRequestDto.class)))
        .thenReturn(successFuture);

    // When
    producer.publish(testRequestDto);

    // Then
    verify(feedbackKafkaTemplate, times(1))
        .send(
            eq(FEEDBACK_REQUEST_TOPIC),
            eq(testRequestDto.userId().toString()),
            any(FeedbackRequestDto.class));

    verify(feedbackFailKafkaTemplate, never())
        .send(anyString(), anyString(), any(FeedbackFailDto.class));
  }

  @Test
  @DisplayName("피드백 요청 발행 실패 시 실패 토픽으로 피드백 실패 DTO가 정확히 전송되어야 한다")
  void 정상_발행_실패_시_트리거_검증() {
    // Given
    RuntimeException testException = new RuntimeException("테스트를 위한 강제 예외");
    CompletableFuture<SendResult<String, FeedbackRequestDto>> failedFuture =
        new CompletableFuture<>();
    failedFuture.completeExceptionally(testException);

    when(feedbackKafkaTemplate.send(
            eq(FEEDBACK_REQUEST_TOPIC),
            eq(testRequestDto.userId().toString()),
            any(FeedbackRequestDto.class)))
        .thenReturn(failedFuture);

    CompletableFuture<SendResult<String, FeedbackFailDto>> failSendSuccessFuture =
        CompletableFuture.completedFuture(new SendResult<>(null, null));

    when(feedbackFailKafkaTemplate.send(
            eq(FEEDBACK_FAIL_TOPIC),
            eq(testRequestDto.userId().toString()),
            any(FeedbackFailDto.class)))
        .thenReturn(failSendSuccessFuture);

    // When
    producer.publish(testRequestDto);

    // Then
    verify(feedbackKafkaTemplate, times(1))
        .send(
            eq(FEEDBACK_REQUEST_TOPIC),
            eq(testRequestDto.userId().toString()),
            any(FeedbackRequestDto.class));

    ArgumentCaptor<FeedbackFailDto> failDtoCaptor = ArgumentCaptor.forClass(FeedbackFailDto.class);

    verify(feedbackFailKafkaTemplate, timeout(100).times(1))
        .send(
            eq(FEEDBACK_FAIL_TOPIC),
            eq(testRequestDto.userId().toString()),
            failDtoCaptor.capture());

    FeedbackFailDto capturedFailDto = failDtoCaptor.getValue();
    assertThat(capturedFailDto.userId()).isEqualTo(testRequestDto.userId());
    assertThat(capturedFailDto.subjectId()).isEqualTo(testRequestDto.subjectId());
    assertThat(capturedFailDto.type()).isEqualTo(testRequestDto.type());
    assertThat(capturedFailDto.nth()).isEqualTo(testRequestDto.nth());
    assertThat(capturedFailDto.errorCode()).isEqualTo("FEEDBACK_REQ_ERROR");
    assertThat(capturedFailDto.errorMessage()).contains(testException.getMessage());
  }
}

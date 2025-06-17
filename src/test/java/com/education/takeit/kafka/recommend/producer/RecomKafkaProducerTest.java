package com.education.takeit.kafka.recommend.producer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.education.takeit.kafka.recommend.dto.RecomFailDto;
import com.education.takeit.kafka.recommend.dto.RecomRequestDto;
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
class RecomKafkaProducerTest {

  @Mock private KafkaTemplate<String, RecomRequestDto> recomKafkaTemplate;

  @Mock private KafkaTemplate<String, RecomFailDto> recomFailKafkaTemplate;

  private RecomKafkaProducer producer;

  private static final String RECOM_REQUEST_TOPIC = "recom.request";
  private static final String RECOM_FAIL_TOPIC = "recom.result.fail";
  private RecomRequestDto testRequestDto;

  @BeforeEach
  void setUp() {
    testRequestDto = new RecomRequestDto(1L, 200L);
    producer = new RecomKafkaProducer(recomKafkaTemplate, recomFailKafkaTemplate);
  }

  @Test
  @DisplayName("성공적인 추천 컨텐츠 요청 발행 시 KafkaTemplate.send가 호출되고 실패 토픽으로 전송되지 않아야 한다")
  void 정상_발행_성공() {
    // Given
    ProducerRecord<String, RecomRequestDto> producerRecord =
        new ProducerRecord<>(
            RECOM_REQUEST_TOPIC, testRequestDto.userId().toString(), testRequestDto);
    RecordMetadata metadata =
        new RecordMetadata(
            new TopicPartition(RECOM_REQUEST_TOPIC, 0), 0L, 0, System.currentTimeMillis(), 0, 0);
    SendResult<String, RecomRequestDto> sendResult = new SendResult<>(producerRecord, metadata);
    CompletableFuture<SendResult<String, RecomRequestDto>> successFuture =
        CompletableFuture.completedFuture(sendResult);

    when(recomKafkaTemplate.send(
            eq(RECOM_REQUEST_TOPIC),
            eq(testRequestDto.userId().toString()),
            any(RecomRequestDto.class) // any() 매처 사용
            ))
        .thenReturn(successFuture);

    // When
    producer.publish(testRequestDto);

    // Then
    verify(recomKafkaTemplate, times(1))
        .send(
            eq(RECOM_REQUEST_TOPIC),
            eq(testRequestDto.userId().toString()),
            any(RecomRequestDto.class) // any() 매처 사용
            );

    verify(recomFailKafkaTemplate, never()).send(anyString(), anyString(), any(RecomFailDto.class));
  }

  @Test
  @DisplayName("추천 컨텐츠 요청 발행 실패 시 실패 토픽으로 추천 실패 DTO가 정확히 전송되어야 한다")
  void 발행_실패_실패_토픽_전송() {
    // Given
    RuntimeException testException = new RuntimeException("테스트를 위한 강제 예외");
    CompletableFuture<SendResult<String, RecomRequestDto>> failedFuture = new CompletableFuture<>();
    failedFuture.completeExceptionally(testException);

    when(recomKafkaTemplate.send(
            eq(RECOM_REQUEST_TOPIC),
            eq(testRequestDto.userId().toString()),
            any(RecomRequestDto.class) // any() 매처 사용
            ))
        .thenReturn(failedFuture);

    // When
    producer.publish(testRequestDto);

    // Then
    verify(recomKafkaTemplate, times(1))
        .send(
            eq(RECOM_REQUEST_TOPIC),
            eq(testRequestDto.userId().toString()),
            any(RecomRequestDto.class) // any() 매처 사용
            );

    ArgumentCaptor<RecomFailDto> failDtoCaptor = ArgumentCaptor.forClass(RecomFailDto.class);
    verify(recomFailKafkaTemplate, timeout(100).times(1))
        .send(
            eq(RECOM_FAIL_TOPIC), eq(testRequestDto.userId().toString()), failDtoCaptor.capture());

    RecomFailDto capturedFailDto = failDtoCaptor.getValue();
    assertThat(capturedFailDto.userId()).isEqualTo(testRequestDto.userId());
    assertThat(capturedFailDto.subjectId()).isEqualTo(testRequestDto.subjectId());
    assertThat(capturedFailDto.errorCode()).isEqualTo("RECOM_REQ_ERROR");
    assertThat(capturedFailDto.errorMessage()).contains(testException.getMessage());
  }
}

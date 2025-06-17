package com.education.takeit.kafka.recommend.consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.education.takeit.kafka.common.slack.SlackNotifier;
import com.education.takeit.kafka.recommend.dto.RecomFailDto;
import com.education.takeit.kafka.recommend.service.RecomFailLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

@ExtendWith(MockitoExtension.class)
class RecomFailKafkaConsumerTest {
  @Mock private RecomFailLogService recomFailLogService; // RecomFailLogService 모의 객체

  @Mock private SlackNotifier slackNotifier; // SlackNotifier 모의 객체

  @Mock private Acknowledgment acknowledgment; // Acknowledgment 모의 객체

  @InjectMocks private RecomFailKafkaConsumer consumer;

  private RecomFailDto testPayload;

  @BeforeEach
  void setUp() {
    // 테스트 페이로드 초기화
    testPayload = new RecomFailDto(2L, 202L, "RECOM_REQ_ERROR", "");
  }

  @Test
  @DisplayName("성공적인 실패 이벤트 수신 시 DB 로그 적재, Slack 알림 전송, Acknowledgment.acknowledge()가 호출되어야 한다")
  void 피드백_실패_이벤트_소비_성공() {
    // Given
    doNothing().when(recomFailLogService).saveFailLog(any(RecomFailDto.class));
    doNothing().when(slackNotifier).send(anyString());
    doNothing().when(acknowledgment).acknowledge();

    // When
    consumer.consumeFail(testPayload, acknowledgment);

    // Then
    verify(recomFailLogService, times(1)).saveFailLog(eq(testPayload));

    ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
    verify(slackNotifier, times(1)).send(messageCaptor.capture());

    String capturedMessage = messageCaptor.getValue();
    assertThat(capturedMessage)
        .contains(
            "[추천 컨텐츠 생성 실패 알림]",
            String.format("- userId: %d", testPayload.userId()),
            String.format("- subjectId: %d", testPayload.subjectId()),
            String.format("- errorCode: %s", testPayload.errorCode()),
            String.format("- errorMessage: %s", testPayload.errorMessage()));
    assertThat(capturedMessage).contains("- timestamp:");

    verify(acknowledgment, times(1)).acknowledge();
  }

  @Test
  @DisplayName("DB 로그 적재 실패 시 Slack 알림 전송 및 Acknowledgment.acknowledge()가 호출되지 않아야 한다")
  void 피드백_실패_이벤트_소비_실패_DB로그_적재() {
    // Given
    RuntimeException dbException = new RuntimeException("DB 저장 실패");
    doThrow(dbException).when(recomFailLogService).saveFailLog(any(RecomFailDto.class));

    // When
    consumer.consumeFail(testPayload, acknowledgment);

    // Then
    verify(recomFailLogService, times(1)).saveFailLog(eq(testPayload));

    verify(slackNotifier, never()).send(anyString());

    verify(acknowledgment, never()).acknowledge();
  }

  @Test
  @DisplayName("Slack 알림 전송 실패 시 DB 로그는 적재되고 Acknowledgment.acknowledge()는 호출되지 않아야 한다")
  void 피드백_실패_이벤트_소비_실패_슬랙_알림_실패() {
    // Given
    doNothing().when(recomFailLogService).saveFailLog(any(RecomFailDto.class));
    RuntimeException slackException = new RuntimeException("Slack 알림 전송 실패");
    doThrow(slackException).when(slackNotifier).send(anyString());

    // When
    consumer.consumeFail(testPayload, acknowledgment);

    // Then
    verify(recomFailLogService, times(1)).saveFailLog(eq(testPayload));

    ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
    verify(slackNotifier, times(1)).send(messageCaptor.capture());
    assertThat(messageCaptor.getValue()).contains(testPayload.errorMessage()); // 메시지 내용 일부 검증

    verify(acknowledgment, never()).acknowledge();
  }
}

package com.education.takeit.kafka.feedback.consumer;

import com.education.takeit.kafka.common.slack.SlackNotifier;
import com.education.takeit.kafka.feedback.dto.FeedbackFailDto;
import com.education.takeit.kafka.feedback.service.FeedbackFailLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackFailKafkaConsumerTest {

    @Mock
    private FeedbackFailLogService feedbackFailLogService;

    @Mock
    private SlackNotifier slackNotifier;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private FeedbackFailKafkaConsumer consumer;

    private FeedbackFailDto testPayload;

    @BeforeEach
    void setUp() {
        testPayload = new FeedbackFailDto(
                1L,
                1L,
                "pre",
                1,
                "FEEDBACK_REQ_ERROR",
                "THIS IS ERROR"
        );
    }

    @Test
    @DisplayName("성공적인 실패 이벤트 수신 시 DB 로그 적재, Slack 알림 전송, Acknowledgment.acknowledge()가 호출되어야 한다")
    void 피드백_실패_이벤트_소비_성공 () {
        // Given
        doNothing().when(feedbackFailLogService).saveFailLog(any(FeedbackFailDto.class));
        doNothing().when(slackNotifier).send(anyString());
        doNothing().when(acknowledgment).acknowledge();

        // When
        consumer.consumeFail(testPayload, acknowledgment);

        // Then
        verify(feedbackFailLogService, times(1)).saveFailLog(eq(testPayload));

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(slackNotifier, times(1)).send(messageCaptor.capture());

        String capturedMessage = messageCaptor.getValue();
        assertThat(capturedMessage).contains(
                String.format("- userId: %d", testPayload.userId()),
                String.format("- subjectId: %d", testPayload.subjectId()),
                String.format("- type: %s", testPayload.type()),
                String.format("- nth: %d", testPayload.nth()),
                String.format("- errorCode: %s", testPayload.errorCode()),
                String.format("- errorMessage: %s", testPayload.errorMessage())
        );
        assertThat(capturedMessage).contains("- timestamp:");

        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    @DisplayName("DB 로그 적재 실패 시 Slack 알림 전송 및 Acknowledgment.acknowledge()가 호출되지 않아야 한다")
    void 피드백_실패_이벤트_소비_실패_DB로그_적재 () {
        // Given
        RuntimeException dbException = new RuntimeException("DB 저장 실패");
        doThrow(dbException).when(feedbackFailLogService).saveFailLog(any(FeedbackFailDto.class));

        // When
        consumer.consumeFail(testPayload, acknowledgment);

        // Then
        verify(feedbackFailLogService, times(1)).saveFailLog(eq(testPayload));
        verify(slackNotifier, never()).send(anyString());
        verify(acknowledgment, never()).acknowledge();
    }

    @Test
    @DisplayName("Slack 알림 전송 실패 시 DB 로그는 적재되고 Acknowledgment.acknowledge()는 호출되지 않아야 한다")
    void 피드백_실패_이벤트_소비_실패_슬랙_알림_실패 () {
        // Given
        doNothing().when(feedbackFailLogService).saveFailLog(any(FeedbackFailDto.class));
        RuntimeException slackException = new RuntimeException("Slack 알림 전송 실패");
        doThrow(slackException).when(slackNotifier).send(anyString());

        // When
        consumer.consumeFail(testPayload, acknowledgment);

        // Then
        verify(feedbackFailLogService, times(1)).saveFailLog(eq(testPayload));

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(slackNotifier, times(1)).send(messageCaptor.capture());
        assertThat(messageCaptor.getValue()).contains(testPayload.errorMessage()); // 메시지 내용 일부 검증

        verify(acknowledgment, never()).acknowledge();
    }
}
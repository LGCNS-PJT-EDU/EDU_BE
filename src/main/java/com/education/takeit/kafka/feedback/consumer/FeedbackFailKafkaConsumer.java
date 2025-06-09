package com.education.takeit.kafka.feedback.consumer;

import com.education.takeit.kafka.common.slack.SlackNotifier;
import com.education.takeit.kafka.feedback.dto.FeedbackFailDto;
import com.education.takeit.kafka.feedback.service.FeedbackFailLogService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedbackFailKafkaConsumer {

  private final FeedbackFailLogService feedbackFailLogService;
  private final SlackNotifier slackNotifier;

  @KafkaListener(
      topics = "feedback.result.fail",
      containerFactory = "feedbackFailKafkaListenerContainerFactory")
  public void consumeFail(@Payload FeedbackFailDto payload, Acknowledgment acknowledgment) {
    log.error("피드백 생성 실패 요청 수신: {}", payload);
    try {
      // 1. DB로그 적재
      feedbackFailLogService.saveFailLog(payload);
      log.info("피드백 DB 로그 적재 완료");

      // 2. Slack 알림 전송
      String message =
          String.format(
              "[피드백 생성 실패 알림]\n"
                  + "- userId: %d\n"
                  + "- subjectId: %d\n"
                  + "- type: %s\n"
                  + "- nth: %d\n"
                  + "- errorCode: %s\n"
                  + "- errorMessage: %s\n"
                  + "- timestamp: %s",
              payload.userId(),
              payload.subjectId(),
              payload.type(),
              payload.nth(),
              payload.errorCode(),
              payload.errorMessage(),
              LocalDateTime.now());
      slackNotifier.send(message);
      acknowledgment.acknowledge();
    } catch (Exception e) {
      log.error("피드백 생성 실패 로그 적재 및 알림 발송 실패", e);
    }
  }
}

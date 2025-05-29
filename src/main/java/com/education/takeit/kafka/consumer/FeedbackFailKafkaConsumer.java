package com.education.takeit.kafka.consumer;

import com.education.takeit.kafka.dto.FeedbackFailDto;
import com.education.takeit.kafka.service.FeedbackFailLogService;
import com.education.takeit.kafka.slack.SlackNotifier;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
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
  public void consumerFail(ConsumerRecord<String, FeedbackFailDto> record) {
    FeedbackFailDto result = record.value();
    log.error("피드백 생성 실패 요청 수신: {}", result);

    // 1. DB로그 적재
    feedbackFailLogService.saveFailLog(result);

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
            result.userId(),
            result.subjectId(),
            result.type(),
            result.nth(),
            result.errorCode(),
            result.errorMessage(),
            LocalDateTime.now());
    slackNotifier.send(message);
  }
}

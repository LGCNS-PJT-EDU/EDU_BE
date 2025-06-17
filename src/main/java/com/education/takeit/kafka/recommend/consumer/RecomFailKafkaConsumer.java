package com.education.takeit.kafka.recommend.consumer;

import com.education.takeit.kafka.common.slack.SlackNotifier;
import com.education.takeit.kafka.recommend.dto.RecomFailDto;
import com.education.takeit.kafka.recommend.service.RecomFailLogService;
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
public class RecomFailKafkaConsumer {

  private final RecomFailLogService recomFailLogService;
  private final SlackNotifier slackNotifier;

  @KafkaListener(
      topics = "recom.result.fail",
      containerFactory = "recomFailKafkaListenerContainerFactory")
  public void consumeFail(@Payload RecomFailDto payload, Acknowledgment acknowledgment) {
    log.error("추천 컨텐츠 생성 실패 요청 수신: {}", payload);
    try {
      // 1. DB로그 적재
      recomFailLogService.saveFailLog(payload);

      // 2. Slack 알림 전송
      String message =
          String.format(
              "[추천 컨텐츠 생성 실패 알림]\n"
                  + "- userId: %d\n"
                  + "- subjectId: %d\n"
                  + "- errorCode: %s\n"
                  + "- errorMessage: %s\n"
                  + "- timestamp: %s",
              payload.userId(),
              payload.subjectId(),
              payload.errorCode(),
              payload.errorMessage(),
              LocalDateTime.now());
      slackNotifier.send(message);
      acknowledgment.acknowledge();
    } catch (Exception e) {
      log.error("추천 컨텐츠 생성 실패 로그 적재 및 알림 발송 실패", e);
    }
  }
}

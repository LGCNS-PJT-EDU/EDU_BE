package com.education.takeit.kafka.common.slack;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SlackNotifier {

  private final RestClient restClient;

  @Value("${slack.webhook-url}")
  private String webhookUrl;

  /**
   * Slack 관리자 알림 발송
   * @param message
   */
  public void send(String message) {
    try {
      restClient
          .post()
          .uri(webhookUrl)
          .contentType(MediaType.APPLICATION_JSON)
          .body(Map.of("text", message))
          .retrieve()
          .toBodilessEntity();

      log.info("Slack 관리자 알림 발송 완료");
    } catch (Exception e) {
      log.error("Slack 관리자 알림 발송 실패", e);
    }
  }
}

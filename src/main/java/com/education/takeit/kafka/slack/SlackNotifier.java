package com.education.takeit.kafka.slack;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class SlackNotifier {

  private final RestClient restClient;

  @Value("${slack.webhook-url}")
  private String webhookUrl;

  public void send(String message) {
    try {
      restClient
          .post()
          .uri(webhookUrl)
          .contentType(MediaType.APPLICATION_JSON)
          .body(Map.of("text", message))
          .retrieve()
          .toBodilessEntity();

      log.info("Slack message sent via RestClient.");
    } catch (Exception e) {
      log.error("Failed to send Slack message via RestClient", e);
    }
  }
}

package com.education.takeit.kafka.producer;

import com.education.takeit.kafka.dto.FeedbackFailDto;
import com.education.takeit.kafka.dto.FeedbackRequestDto;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackKafkaProducer {

  private static final String FEEDBACK_TOPIC = "feedback.request";
  private static final String FEEDBACK_FAIL_TOPIC = "feedback.result.fail";
  private static final int MAX_ATTEMPT = 4;

  private final KafkaTemplate<String, FeedbackRequestDto> feedbackKafkaTemplate;
  private final KafkaTemplate<String, FeedbackFailDto> feedbackFailKafkaTemplate;

  private static final ScheduledExecutorService RETRY_EXECUTOR =
      Executors.newSingleThreadScheduledExecutor(
          r -> {
            Thread t = new Thread(r, "kafka-retry-exec");
            t.setDaemon(true);
            return t;
          });

  public void publish(FeedbackRequestDto dto) {
    sendWithRetry(dto, 0);
  }

  private void sendWithRetry(FeedbackRequestDto dto, int attempt) {
    try {
      if (true) {
        throw new RuntimeException("테스트 예외 발생");
      }
      feedbackKafkaTemplate
          .send(FEEDBACK_TOPIC, dto.userId().toString(), dto)
          .toCompletableFuture()
          .thenAccept(
              result -> {
                var m = result.getRecordMetadata();
                log.info(
                    "✅ Kafka sent topic={} partition={} offset={}",
                    m.topic(),
                    m.partition(),
                    m.offset());
              })
          .exceptionally(
              ex -> {
                handleRetryOrFail(dto, attempt, ex);
                return null;
              });
    } catch (Exception ex) {
      handleRetryOrFail(dto, attempt, ex);
    }
  }

  private void handleRetryOrFail(FeedbackRequestDto dto, int attempt, Throwable ex) {
    if (attempt < MAX_ATTEMPT) {
      long backoff = (long) (200 * Math.pow(2, attempt)); // 0.2s, 0.4s
      log.warn(
          "retry {}/{} in {} ms (cause={})", attempt + 1, MAX_ATTEMPT, backoff, ex.getMessage());

      RETRY_EXECUTOR.schedule(
          () -> sendWithRetry(dto, attempt + 1), backoff, TimeUnit.MILLISECONDS);
    } else {
      log.error("publish failed -> FAIL_TOPIC, key={}, reason={}", dto.userId(), ex.getMessage());
      feedbackFailKafkaTemplate.send(
          FEEDBACK_FAIL_TOPIC,
          dto.userId().toString(),
          new FeedbackFailDto(
              dto.userId(),
              dto.subjectId(),
              dto.type(),
              dto.nth(),
              "FEEDBACK_REQ_ERROR",
              ex.getMessage()));
    }
  }
}

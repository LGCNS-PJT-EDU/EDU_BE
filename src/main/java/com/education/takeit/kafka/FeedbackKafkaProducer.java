package com.education.takeit.kafka;

import com.education.takeit.kafka.dto.DlqWrapper;
import com.education.takeit.kafka.dto.FeedbackEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackKafkaProducer {

    private static final String FEEDBACK_TOPIC = "feedback.request";
    private static final String DQL_TOPIC = "feedback.dlq";
    private static final int    MAX_ATTEMPT = 4;

    private final KafkaTemplate<String, FeedbackEventDto> feedbackKafkaTemplate;
    private final KafkaTemplate<String, DlqWrapper> dlqKafkaTemplate;

    private static final ScheduledExecutorService RETRY_EXECUTOR =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "kafka-retry-exec");
                t.setDaemon(true);
                return t;
            });


    public void publish(FeedbackEventDto dto) {
        sendWithRetry(dto, 0);
    }

    private void sendWithRetry(FeedbackEventDto dto, int attempt) {
        feedbackKafkaTemplate.send(FEEDBACK_TOPIC, dto.userId().toString(), dto)
                .toCompletableFuture()
                .thenAccept(result -> {
                    var m = result.getRecordMetadata();
                    log.info("✅ Kafka sent topic={} partition={} offset={}",
                            m.topic(), m.partition(), m.offset());
                })
                .exceptionally(ex -> {
                    if (attempt < MAX_ATTEMPT) {
                        long backoff = (long) (200 * Math.pow(2, attempt)); // 0.2s → 0.4s…
                        log.warn("retry {}/{} in {} ms (cause={})",
                                attempt + 1, MAX_ATTEMPT, backoff, ex.getMessage());

                        RETRY_EXECUTOR.schedule(
                                () -> sendWithRetry(dto, attempt + 1),
                                backoff, TimeUnit.MILLISECONDS);
                    } else {
                        log.error("publish failed -> DLQ, key={}",
                                dto.userId(), ex);
                        dlqKafkaTemplate.send(DQL_TOPIC,
                                dto.userId().toString(),
                                new DlqWrapper(dto, ex.getMessage(), System.currentTimeMillis())
                        );
                    }
                    return null;
                });
    }
}

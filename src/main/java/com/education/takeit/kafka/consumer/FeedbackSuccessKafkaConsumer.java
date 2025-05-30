package com.education.takeit.kafka.consumer;

import com.education.takeit.feedback.service.FeedbackService;
import com.education.takeit.kafka.dto.FeedbackResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedbackSuccessKafkaConsumer {

  private final FeedbackService feedbackService;

  @KafkaListener(
      topics = "feedback.result.success",
      containerFactory = "feedbackSuccessKafkaListenerContainerFactory")
  public void consumeSuccess(@Payload FeedbackResultDto payload, Acknowledgment acknowledgment) {
    log.info("성공 피드백 수신: {}", payload);
    try {
      feedbackService.saveFeedback(payload);
      log.info("성공 피드백 저장");
      acknowledgment.acknowledge();
    } catch (Exception e) {
      log.error("Failed to process feedback.request.success", e);
      throw new RuntimeException(e);
    }
  }
}

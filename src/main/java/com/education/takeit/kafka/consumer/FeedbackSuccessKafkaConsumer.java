package com.education.takeit.kafka.consumer;

import com.education.takeit.feedback.service.FeedbackService;
import com.education.takeit.kafka.dto.FeedbackResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedbackSuccessKafkaConsumer {

  private final FeedbackService feedbackService;

  @KafkaListener(
      topics = "feedback.result.success",
      containerFactory = "feedbackSuccessKafkaListenerContainerFactory")
  public void consumeSuccess(ConsumerRecord<String, FeedbackResultDto> record) {
    FeedbackResultDto result = record.value();
    log.info("성공 피드백 수신: {}", result);
    // feedback Save
  }
}

package com.education.takeit.kafka.producer;

import com.education.takeit.kafka.dto.FeedbackFailDto;
import com.education.takeit.kafka.dto.FeedbackRequestDto;
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

  public void publish(FeedbackRequestDto dto) {
      feedbackKafkaTemplate.send(FEEDBACK_TOPIC, dto.userId().toString(), dto)
              .whenComplete((result, ex) -> {
                  if (ex == null) {
                      var m = result.getRecordMetadata();
                      log.info(
                              "Kafka sent topic={} partition={} offset={}",
                              m.topic(),
                              m.partition(),
                              m.offset());
                  } else {
                      log.error("Final published: {}", ex.getMessage());
                      sendToFailTopic(dto, ex);
                  }
              });
  }

    private void sendToFailTopic(FeedbackRequestDto dto, Throwable ex) {
        FeedbackFailDto failDto = new FeedbackFailDto(
                dto.userId(),
                dto.subjectId(),
                dto.type(),
                dto.nth(),
                "FEEDBACK_REQ_ERROR",
                ex.getMessage());
        feedbackFailKafkaTemplate.send(FEEDBACK_FAIL_TOPIC, dto.userId().toString(), failDto);
    }
}

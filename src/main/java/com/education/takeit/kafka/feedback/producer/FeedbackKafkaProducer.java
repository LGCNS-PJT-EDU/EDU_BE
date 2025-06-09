package com.education.takeit.kafka.feedback.producer;

import com.education.takeit.kafka.feedback.dto.FeedbackFailDto;
import com.education.takeit.kafka.feedback.dto.FeedbackRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackKafkaProducer {

  private static final String FEEDBACK_REQUEST_TOPIC = "feedback.request";
  private static final String FEEDBACK_FAIL_TOPIC = "feedback.result.fail";

  private final KafkaTemplate<String, FeedbackRequestDto> feedbackKafkaTemplate;
  private final KafkaTemplate<String, FeedbackFailDto> feedbackFailKafkaTemplate;

  /**
   * 피드백 생성 요청 이벤트 발행
   *
   * @param dto
   */
  public void publish(FeedbackRequestDto dto) {
    feedbackKafkaTemplate
        .send(FEEDBACK_REQUEST_TOPIC, dto.userId().toString(), dto)
        .whenComplete(
            (result, ex) -> {
              if (ex == null) {
                var m = result.getRecordMetadata();
                log.info(
                    "피드백 생성 발송 sent topic={} partition={} offset={}",
                    m.topic(),
                    m.partition(),
                    m.offset());
              } else {
                log.error("피드백 생성 발송 실패: {}", ex.getMessage());
                sendToFailTopic(dto, ex);
              }
            });
  }

  /**
   * 피드백 생성 요청 이벤트 발행 실패 -> 피드백 생성 실패 이벤트 발행
   *
   * @param dto
   * @param ex
   */
  private void sendToFailTopic(FeedbackRequestDto dto, Throwable ex) {
    FeedbackFailDto failDto =
        new FeedbackFailDto(
            dto.userId(),
            dto.subjectId(),
            dto.type(),
            dto.nth(),
            "FEEDBACK_REQ_ERROR",
            ex.getMessage());
    feedbackFailKafkaTemplate.send(FEEDBACK_FAIL_TOPIC, dto.userId().toString(), failDto);
    log.info("피드백 생성 발행 실패");
  }
}

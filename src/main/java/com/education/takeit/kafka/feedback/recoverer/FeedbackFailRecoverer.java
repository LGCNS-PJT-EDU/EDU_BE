package com.education.takeit.kafka.feedback.recoverer;

import com.education.takeit.kafka.feedback.dto.FeedbackFailDto;
import com.education.takeit.kafka.feedback.dto.FeedbackResultDto;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;

public class FeedbackFailRecoverer extends DeadLetterPublishingRecoverer {
  public FeedbackFailRecoverer(KafkaOperations<?, ?> kafkaOperations) {
    super(kafkaOperations);
  }

  @Override
  protected ProducerRecord<Object, Object> createProducerRecord(
      ConsumerRecord<?, ?> record,
      TopicPartition topicPartition,
      Headers headers,
      byte[] key,
      byte[] value) {
    // 원본 메시지 추출 (FeedbackRequestDto)
    FeedbackResultDto originalRequest = (FeedbackResultDto) record.value();

    // 예외 정보 헤더 추출
    String errorCode = "FEEDBACK_SAVE_ERROR";
    String errorMessage = "피드백 저장 실패 오류, 자세한 내용은 로그를 확인하세요.";

    // 실패 DTO 생성
    FeedbackFailDto failDto =
        new FeedbackFailDto(
            originalRequest.userId(),
            originalRequest.subjectId(),
            originalRequest.type(),
            originalRequest.nth(),
            errorCode,
            errorMessage);

    // 실패 메시지 전송용 ProducerRecord 생성
    return new ProducerRecord<>(
        "feedback.result.fail", // 실패 토픽
        null,
        record.timestamp(),
        record.key(),
        failDto,
        headers);
  }
}

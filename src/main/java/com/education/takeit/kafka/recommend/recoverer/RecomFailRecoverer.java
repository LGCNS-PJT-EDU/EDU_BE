package com.education.takeit.kafka.recommend.recoverer;

import com.education.takeit.kafka.recommend.dto.RecomFailDto;
import com.education.takeit.kafka.recommend.dto.RecomResultDto;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;

public class RecomFailRecoverer extends DeadLetterPublishingRecoverer {
  public RecomFailRecoverer(KafkaOperations<?, ?> kafkaOperations) {
    super(kafkaOperations);
  }

  @Override
  protected ProducerRecord<Object, Object> createProducerRecord(
      ConsumerRecord<?, ?> record,
      TopicPartition topicPartition,
      Headers headers,
      byte[] key,
      byte[] value) {
    RecomResultDto originalRequest = (RecomResultDto) record.value();

    String errorCode = "RECOM_SAVE_ERROR";
    String errorMessage = "추천 컨텐츠 저장 실패 오류, 자세한 내용은 로그를 확인하세요.";

    RecomFailDto failDto =
        new RecomFailDto(
            originalRequest.userId(), originalRequest.subjectId(), errorCode, errorMessage);
    return new ProducerRecord<>(
        "recom.result.fail", null, record.timestamp(), record.key(), failDto, headers);
  }
}

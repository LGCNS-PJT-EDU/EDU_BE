package com.education.takeit.kafka.recommend.consumer;

import com.education.takeit.kafka.recommend.dto.RecomResultDto;
import com.education.takeit.recommend.service.RecommendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecomSuccessKafkaConsumer {

  private final RecommendService recommendService;

  @KafkaListener(
      topics = "recom.result.success",
      containerFactory = "recomSuccessKafkaListenerContainerFactory")
  public void consumeSuccess(@Payload RecomResultDto payload, Acknowledgment acknowledgment) {
    log.info("추천 컨텐츠 수신: {}", payload);
    try {
      recommendService.saveUserContents(payload);
      acknowledgment.acknowledge();
      log.info("추천 컨텐츠 저장 성공");
    } catch (Exception e) {
      log.error("추천 컨텐츠 저장 실패", e);
      throw new RuntimeException(e);
    }
  }
}

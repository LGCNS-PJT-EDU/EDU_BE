package com.education.takeit.kafka.config;

import com.education.takeit.kafka.dto.FeedbackFailDto;
import com.education.takeit.kafka.dto.FeedbackRequestDto;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaProducerConfig {

  @Value("${spring.kafka.bootstrap-servers}")
  private String servers;

  /* 피드맥 토픽용 Template */
  @Bean
  public KafkaTemplate<String, FeedbackRequestDto> feedbackKafkaTemplate() {
    return new KafkaTemplate<>(producerFactory(FeedbackRequestDto.class));
  }

  /* 피드백 실패 토픽용 Template */
  @Bean
  public KafkaTemplate<String, FeedbackFailDto> feedbackFailKafkaTemplate() {
    return new KafkaTemplate<>(producerFactory(FeedbackFailDto.class));
  }

  private <V> ProducerFactory<String, V> producerFactory(Class<V> clazz) {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    // 신뢰성 보장을 위한 핵심 설정
    props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true); // 해당 설정을 켜면 메세지를 한번만 발송하게 됨
    props.put(ProducerConfig.ACKS_CONFIG, "all"); // 리더 + ISR 모두 커밋 후 OK
    props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1); // 1로 낮춰 순서 보장 강화
    //    // 재시도 관련 최적화
    //    props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 120_000); //총 재시도 시간 2분
    //    props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE); // 재시도 허용
    //    props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1_000);
    // 재시도 테스트용 설정
    props.put(ProducerConfig.RETRIES_CONFIG, 5); // 5회 재시도
    props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1_000); // 1초 간격
    return new DefaultKafkaProducerFactory<>(props);
  }
}

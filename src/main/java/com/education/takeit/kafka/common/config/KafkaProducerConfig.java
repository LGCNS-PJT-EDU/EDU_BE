package com.education.takeit.kafka.common.config;

import com.education.takeit.kafka.feedback.dto.FeedbackFailDto;
import com.education.takeit.kafka.feedback.dto.FeedbackRequestDto;
import com.education.takeit.kafka.recommand.dto.RecomFailDto;
import com.education.takeit.kafka.recommand.dto.RecomRequestDto;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

  @Value("${spring.kafka.bootstrap-servers}")
  private String servers;

  /**
   * 피드백 생성 요청 템플릿
   * @return
   */
  @Bean
  public KafkaTemplate<String, FeedbackRequestDto> feedbackKafkaTemplate() {
    return new KafkaTemplate<>(producerFactory(FeedbackRequestDto.class));
  }

  /**
   * 피드백 생성 실패 템플릿
   * @return
   */
  @Bean
  public KafkaTemplate<String, FeedbackFailDto> feedbackFailKafkaTemplate() {
    return new KafkaTemplate<>(producerFactory(FeedbackFailDto.class));
  }

  /**
   * 추천 컨텐츠 생성 요청 템플릿
   * @return
   */
  @Bean
  public KafkaTemplate<String, RecomRequestDto> recomKafkaTemplate() {
    return new KafkaTemplate<>(producerFactory(RecomRequestDto.class));
  }

  /**
   * 추천 컨텐츠 생성 실패 템플릿
   * @return
   */
  @Bean
  public KafkaTemplate<String, RecomFailDto> recomFailKafkaTemplate() {
    return new KafkaTemplate<>(producerFactory(RecomFailDto.class));
  }

  private <V> ProducerFactory<String, V> producerFactory(Class<V> clazz) {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
    props.put(ProducerConfig.ACKS_CONFIG, "all");
    props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
    //    // 재시도 관련 최적화
    //    props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 120_000); //총 재시도 시간 2분
    //    props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE); // 재시도 허용
    //    props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1_000);
    // 재시도 테스트용 설정
    props.put(ProducerConfig.RETRIES_CONFIG, 5);
    props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1_000);
    return new DefaultKafkaProducerFactory<>(props);
  }
}

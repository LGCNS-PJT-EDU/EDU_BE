package com.education.takeit.kafka.config;

import com.education.takeit.kafka.dto.FeedbackFailDto;
import com.education.takeit.kafka.dto.FeedbackResultDto;
import com.education.takeit.kafka.recoverer.FeedbackFailRecoverer;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
public class KafkaConsumerConfig {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  private static final String TRUSTED_PACKAGE = "com.education.takeit.kafka.dto";

  @Bean
  public DefaultErrorHandler feedbackErrorHandler(
      @Qualifier("feedbackFailKafkaTemplate")
          KafkaOperations<String, FeedbackFailDto> feedbackFailKafkaTemplate) {
    // 커스텀 recoverer
    FeedbackFailRecoverer recoverer = new FeedbackFailRecoverer(feedbackFailKafkaTemplate);
    // 재시도 정책 (3회 재시도, 지수백오프)
    ExponentialBackOffWithMaxRetries backOff = new ExponentialBackOffWithMaxRetries(3); // 3회 재시도
    backOff.setInitialInterval(1000L);
    backOff.setMultiplier(2.0);

    DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);
    errorHandler.addRetryableExceptions(Exception.class); // 모든 예외 재시도
    errorHandler.addNotRetryableExceptions(); // 특정 예외 제외 없음
    errorHandler.setCommitRecovered(true); // 실패 후 오프셋 커밋
    // 재시도 실패 시, recoverer가 실패 토픽으로 전송
    return errorHandler;
  }

  @Bean
  public ConsumerFactory<String, FeedbackResultDto> feedbackSuccessConsumerFactory() {
    JsonDeserializer<FeedbackResultDto> deserializer =
        new JsonDeserializer<>(FeedbackResultDto.class);
    deserializer.addTrustedPackages(TRUSTED_PACKAGE);
    deserializer.setUseTypeHeaders(false);

    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "feedback-result-success-group");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  @Bean
  public ConsumerFactory<String, FeedbackFailDto> feedbackFailConsumerFactory() {
    JsonDeserializer<FeedbackFailDto> deserializer = new JsonDeserializer<>(FeedbackFailDto.class);
    deserializer.addTrustedPackages(TRUSTED_PACKAGE);
    deserializer.setUseTypeHeaders(false);

    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "feedback-result-fail-group");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, FeedbackResultDto>
      feedbackSuccessKafkaListenerContainerFactory(DefaultErrorHandler errorHandler) {
    ConcurrentKafkaListenerContainerFactory<String, FeedbackResultDto> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(feedbackSuccessConsumerFactory());
    factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE); // 수동 커밋 모드
    factory.setCommonErrorHandler(errorHandler);
    return factory;
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, FeedbackFailDto>
      feedbackFailKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, FeedbackFailDto> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(feedbackFailConsumerFactory());
    factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE); // 수동 커밋 모드
    return factory;
  }
}

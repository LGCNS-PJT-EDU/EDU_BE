package com.education.takeit.kafka.common.config;

import com.education.takeit.kafka.feedback.dto.FeedbackFailDto;
import com.education.takeit.kafka.feedback.dto.FeedbackResultDto;
import com.education.takeit.kafka.feedback.recoverer.FeedbackFailRecoverer;
import com.education.takeit.kafka.recommend.dto.RecomFailDto;
import com.education.takeit.kafka.recommend.dto.RecomResultDto;
import com.education.takeit.kafka.recommend.recoverer.RecomFailRecoverer;
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
  private static final String EARLIEST = "earliest";

  /**
   * 피드백 생성 실패 커스텀 에러 핸들러
   *
   * @param feedbackFailKafkaTemplate
   * @return
   */
  @Bean
  public DefaultErrorHandler feedbackErrorHandler(
      @Qualifier("feedbackFailKafkaTemplate")
          KafkaOperations<String, FeedbackFailDto> feedbackFailKafkaTemplate) {
    FeedbackFailRecoverer recoverer = new FeedbackFailRecoverer(feedbackFailKafkaTemplate);
    ExponentialBackOffWithMaxRetries backOff = new ExponentialBackOffWithMaxRetries(3);
    backOff.setInitialInterval(1000L);
    backOff.setMultiplier(2.0);

    DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);
    errorHandler.addRetryableExceptions(Exception.class);
    errorHandler.addNotRetryableExceptions();
    errorHandler.setCommitRecovered(true);
    return errorHandler;
  }

  /**
   * 추천 컨텐츠 생성 실패 커스텀 에러 핸들러
   *
   * @param recomFailKafkaTemplate
   * @return
   */
  @Bean
  public DefaultErrorHandler recomErrorHandler(
      @Qualifier("recomFailKafkaTemplate")
          KafkaOperations<String, RecomFailDto> recomFailKafkaTemplate) {
    RecomFailRecoverer recoverer = new RecomFailRecoverer(recomFailKafkaTemplate);
    ExponentialBackOffWithMaxRetries backOff = new ExponentialBackOffWithMaxRetries(3);
    backOff.setInitialInterval(1000L);
    backOff.setMultiplier(2.0);

    DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);
    errorHandler.addRetryableExceptions(Exception.class);
    errorHandler.addNotRetryableExceptions();
    errorHandler.setCommitRecovered(true);
    return errorHandler;
  }

  /**
   * 피드백 생성 성공 컨슈머
   *
   * @return
   */
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
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, EARLIEST);
    props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 900000); // 15분
    props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000); // 세션 유지 시간 (기본: 10초)
    props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 10000); // heartbeat 간격 (기본: 3초)

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  /**
   * 피드백 생성 실패 컨슈머
   *
   * @return
   */
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
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, EARLIEST);
    props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 900000); // 15분
    props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000); // 세션 유지 시간 (기본: 10초)
    props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 10000); // heartbeat 간격 (기본: 3초)

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  /**
   * 추천 컨텐츠 생성 성공 컨슈머
   *
   * @return
   */
  @Bean
  public ConsumerFactory<String, RecomResultDto> recomSuccessConsumerFactory() {
    JsonDeserializer<RecomResultDto> deserializer = new JsonDeserializer<>(RecomResultDto.class);
    deserializer.addTrustedPackages(TRUSTED_PACKAGE);
    deserializer.setUseTypeHeaders(false);

    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "recom-result-success-group");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, EARLIEST);

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  /**
   * 추천 컨텐츠 생성 실패 컨슈머
   *
   * @return
   */
  @Bean
  public ConsumerFactory<String, RecomFailDto> recomFailConsumerFactory() {
    JsonDeserializer<RecomFailDto> deserializer = new JsonDeserializer<>(RecomFailDto.class);
    deserializer.addTrustedPackages(TRUSTED_PACKAGE);
    deserializer.setUseTypeHeaders(false);

    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "recom-result-fail-group");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, EARLIEST);

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, FeedbackResultDto>
      feedbackSuccessKafkaListenerContainerFactory(
          @Qualifier("feedbackErrorHandler") DefaultErrorHandler errorHandler) {
    ConcurrentKafkaListenerContainerFactory<String, FeedbackResultDto> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(feedbackSuccessConsumerFactory());
    factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE);
    factory.setCommonErrorHandler(errorHandler);
    return factory;
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, FeedbackFailDto>
      feedbackFailKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, FeedbackFailDto> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(feedbackFailConsumerFactory());
    factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE);
    return factory;
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, RecomResultDto>
      recomSuccessKafkaListenerContainerFactory(
          @Qualifier("recomErrorHandler") DefaultErrorHandler errorHandler) {
    ConcurrentKafkaListenerContainerFactory<String, RecomResultDto> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(recomSuccessConsumerFactory());
    factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE);
    factory.setCommonErrorHandler(errorHandler);
    return factory;
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, RecomFailDto>
      recomFailKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, RecomFailDto> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(recomFailConsumerFactory());
    factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE);
    return factory;
  }
}

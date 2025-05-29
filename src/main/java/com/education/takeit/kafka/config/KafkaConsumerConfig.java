package com.education.takeit.kafka.config;

import com.education.takeit.kafka.dto.FeedbackFailDto;
import com.education.takeit.kafka.dto.FeedbackResultDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  private static final String TRUSTED_PACKAGE = "com.education.takeit.kafka.dto";

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
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

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
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, FeedbackResultDto>
      feedbackSuccessKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, FeedbackResultDto> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(feedbackSuccessConsumerFactory());
    return factory;
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, FeedbackFailDto>
      feedbackFailKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, FeedbackFailDto> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(feedbackFailConsumerFactory());
    return factory;
  }
}

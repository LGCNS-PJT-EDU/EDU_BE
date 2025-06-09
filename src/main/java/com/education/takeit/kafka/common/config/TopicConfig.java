package com.education.takeit.kafka.common.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@ConditionalOnProperty(value = "kafka.topics.auto-create", matchIfMissing = true)
public class TopicConfig {

  /**
   * 피드백 생성 요청 이벤트 토픽
   * @return
   */
  @Bean
  public NewTopic feedbackRequestTopic() {
    return TopicBuilder.name("feedback.request").partitions(3).replicas(1).build();
  }

  /**
   * 피드백 생성 성공 이벤트 토픽
   * @return
   */
  @Bean
  public NewTopic feedbackResultSuccessTopic() {
    return TopicBuilder.name("feedback.result.success").partitions(3).replicas(1).build();
  }

  /**
   * 피드백 생성 실패 이벤트 토픽
   * @return
   */
  @Bean
  public NewTopic feedbackResultFailTopic() {
    return TopicBuilder.name("feedback.result.fail").partitions(3).replicas(1).build();
  }

  /**
   * 추천 컨텐츠 생성 요청 이벤트 토픽
   * @return
   */
  @Bean
  public NewTopic recomRequestTopic() {
    return TopicBuilder.name("recom.request").partitions(3).replicas(1).build();
  }

  /**
   * 추천 컨텐츠 생성 성공 이벤트 토픽
   * @return
   */
  @Bean
  public NewTopic recomResultSuccessTopic() {
    return TopicBuilder.name("recom.result.success").partitions(3).replicas(1).build();
  }

  /**
   * 추천 컨텐츠 생성 실패 이벤트 토픽
   * @return
   */
  @Bean
  public NewTopic recomResultFailTopic() {
    return TopicBuilder.name("recom.result.fail").partitions(3).replicas(1).build();
  }
}

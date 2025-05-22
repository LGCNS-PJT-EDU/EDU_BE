package com.education.takeit.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@ConditionalOnProperty(value = "kafka.topics.auto-create", matchIfMissing = true)
public class TopicConfig {

    @Bean
    public NewTopic feedbackRequestTopic() {
        return TopicBuilder.name("feedback.request")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic feedbackDlqTopic() {
        return TopicBuilder.name("feedback.dql")
                .partitions(3)
                .replicas(1)
                .build();
    }
}

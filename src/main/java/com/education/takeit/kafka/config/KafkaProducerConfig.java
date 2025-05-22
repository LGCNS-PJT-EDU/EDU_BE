package com.education.takeit.kafka.config;

import com.education.takeit.kafka.dto.DlqWrapper;
import com.education.takeit.kafka.dto.FeedbackEventDto;
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

    /* 피드맥 토픽용 Template */
    @Bean
    public KafkaTemplate<String, FeedbackEventDto> feedbackKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory(FeedbackEventDto.class));
    }

    /* DQL 토픽용 Template */
    @Bean
    public KafkaTemplate<String, DlqWrapper> dlqKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory(DlqWrapper.class));
    }

    private <V> ProducerFactory<String, V> producerFactory(Class<V> clazz) {
        Map<String,Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,  StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true); // 해당 설정을 켜면 메세지를 한번만 발송하게 됨
        props.put(ProducerConfig.ACKS_CONFIG, "all"); //리더 + ISR 모두 커밋 후 OK
        props.put(ProducerConfig.RETRIES_CONFIG, 5); // 재시도 허용
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);// 메세지 응답을 받지 않은 상태에서전송할 최대 메세지 수
        return new DefaultKafkaProducerFactory<>(props);
    }
}

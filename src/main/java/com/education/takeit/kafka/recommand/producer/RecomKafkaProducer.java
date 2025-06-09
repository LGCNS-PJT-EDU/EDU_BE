package com.education.takeit.kafka.recommand.producer;

import com.education.takeit.kafka.recommand.dto.RecomFailDto;
import com.education.takeit.kafka.recommand.dto.RecomRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecomKafkaProducer {

    private static final String RECOM_REQUEST_TOPIC = "recom.request";
    private static final String RECOM_FAIL_TOPIC = "recom.result.fail";

    private final KafkaTemplate<String, RecomRequestDto> recomKafkaTemplate;
    private final KafkaTemplate<String, RecomFailDto> recomFailKafkaTemplate;

    /**
     * 추천 컨텐츠 생성 이벤트 발행
     * @param dto
     */
    public void publish (RecomRequestDto dto) {
        recomKafkaTemplate
                .send(RECOM_REQUEST_TOPIC, dto.userId().toString(), dto)
                .whenComplete(
                        (result, ex) -> {
                            if (ex == null) {
                                var m = result.getRecordMetadata();
                                log.info(
                                        "추천 컨텐츠 생성 발송 topic={} partition={} offset={}",
                                        m.topic(),
                                        m.partition(),
                                        m.offset()
                                );
                            } else {
                                log.error("추천 컨텐츠 생성 발송 실패: {}", ex.getMessage());
                                sentToFailTopic(dto, ex);
                            }
                        }
                );
    }

    /**
     * 추천 컨텐츠 생성 이벤트 발행 실패 -> 추천 컨텐츠 생성 실패 이벤트 발행
     * @param dto
     * @param ex
     */
    private void sentToFailTopic(RecomRequestDto dto, Throwable ex) {
        RecomFailDto failDto =
                new RecomFailDto(
                        dto.userId(),
                        dto.subjectId(),
                        "RECOM_REQ_ERROR",
                        ex.getMessage()
                );
        recomFailKafkaTemplate.send(RECOM_FAIL_TOPIC, dto.userId().toString(), failDto);
        log.info("추천 컨텐츠 생성 발행 실패");
    }
}

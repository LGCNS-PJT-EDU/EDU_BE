package com.education.takeit.kafka.recommand.consumer;

import com.education.takeit.kafka.recommand.dto.RecomResultDto;
import com.education.takeit.recommend.dto.UserContentResDto;
import com.education.takeit.recommend.service.RecommendService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecomSuccessKafkaConsumerTest {

    @Mock
    private RecommendService recommendService;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private RecomSuccessKafkaConsumer consumer;

    private RecomResultDto testPayload;

    @BeforeEach
    void setUp() {
        UserContentResDto content1 = new UserContentResDto(
                1001L, 201L, "Kafka Basics", "http://example.com/kafka", "video", "YouTube", "60min", "Free", true, "Good intro"
        );
        UserContentResDto content2 = new UserContentResDto(
                1002L, 201L, "Spring Kafka Deep Dive", "http://example.com/spring", "article", "Blog", "30min", "Paid", false, "Advanced topic"
        );

        testPayload = new RecomResultDto(
                1L,
                1L,
                Arrays.asList(content1, content2)
        );
    }

    @Test
    @DisplayName("성공적인 추천 컨텐츠 수신 시 RecommendService.saveUserContents가 호출되고 Acknowledgment.acknowledge()가 호출되어야 한다")
    void 추천_컨텐츠_성공_이벤트_소비_성공() {
        // Given
        doNothing().when(recommendService).saveUserContents(any(RecomResultDto.class));

        // When
        consumer.consumeSuccess(testPayload, acknowledgment);

        // Then
        verify(recommendService, times(1)).saveUserContents(any(RecomResultDto.class));

        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    @DisplayName("추천 컨텐츠 저장 실패 시 RecommendService.saveUserContents에서 예외가 발생하고 Acknowledgment.acknowledge()가 호출되지 않아야 한다")
    void consumeSuccess_UnhappyPath_ShouldHandleExceptionAndNotAcknowledge() {
        // Given
        RuntimeException serviceException = new RuntimeException("추천 컨텐츠 저장 중 데이터베이스 오류 발생");
        doThrow(serviceException).when(recommendService).saveUserContents(any(RecomResultDto.class));

        // When & Then
        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            consumer.consumeSuccess(testPayload, acknowledgment);
        });

        verify(recommendService, times(1)).saveUserContents(any(RecomResultDto.class));

        verify(acknowledgment, never()).acknowledge();

        assertThat(thrownException.getCause()).isEqualTo(serviceException);
    }

}
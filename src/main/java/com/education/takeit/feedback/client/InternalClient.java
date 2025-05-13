package com.education.takeit.feedback.client;

import com.education.takeit.feedback.dto.FeedbackResponseDto;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Component
public class InternalClient {

  private final RestClient restClient;

  public List<FeedbackResponseDto> getFeedback(String userId) {
    /*
        Spring Boot 측 URI: /api/feedback/retrieve?userId={userId}
        FastAPI측 URI: /api/feedback?userId={userId}
        FastAPI측 Method: GET
        FastAPI측 Return: 항상 [] or [ {feedback1}, {feedback2} ] 중 하나
        -> FastAPI 서버에 요청이 전송된다면 반환 시 오류가 발생하지 않는다면 항상 Null은 발생하지 않는다
     */
    FeedbackResponseDto[] arr =
        restClient
            .get()
            .uri("http://localhost:8000/api/feedback?userId={userId}", userId)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            // Mono 사용 시 Publisher 사용 안함 오류 발생하므로 주의
            .onStatus(
                status -> !status.is2xxSuccessful(),
                (request, response) -> {
                  throw new CustomException(StatusCode.CONNECTION_FAILED);
                })
            .body(FeedbackResponseDto[].class);

    return Arrays.asList(arr);
  }
}

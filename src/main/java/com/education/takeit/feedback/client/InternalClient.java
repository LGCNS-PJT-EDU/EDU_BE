package com.education.takeit.feedback.client;

import com.education.takeit.feedback.dto.FeedbackResponse;
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

  public List<FeedbackResponse> getFeedback(String userId) {
    // FastAPI측 URI: /feedback?userId={userId}
    // FastAPI측 Method: GET
    // FastAPI측 Return: 항상 [] or [ {feedback1}, {feedback2} ] 중 하나 -> FastAPI 서버에 요청이 전송된다면 반환 시 오류가
    // 발생한 것이 아니면 204 No Content는 반환되지 않음(반환 데이터가 Null이 아님)
    FeedbackResponse[] arr =
        restClient
            .get()
            .uri("http://localhost:8000/feedback?userId={userId}", userId)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            // Mono 사용 시 Publisher 사용 안함 오류 발생하므로 주의
            .onStatus(
                status -> !status.is2xxSuccessful(),
                (request, response) -> {
                  throw new CustomException(StatusCode.CONNECTION_FAILED);
                })
            .onStatus(
                status -> status.value() == HttpStatus.NO_CONTENT.value(),
                (request, response) -> {
                  throw new CustomException(StatusCode.CONNECTION_SUCCESS_BUT_FETCH_FAILED);
                })
            .body(FeedbackResponse[].class);

    Objects.requireNonNull(arr);
    return Arrays.asList(arr);
  }
}

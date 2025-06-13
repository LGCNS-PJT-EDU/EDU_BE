package com.education.takeit.global.client;

import com.education.takeit.exam.dto.ExamResDto;
import com.education.takeit.exam.dto.ExamResultDto;
import com.education.takeit.feedback.dto.FeedbackResponseDto;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.interview.dto.AiFeedbackReqDto;
import com.education.takeit.interview.dto.InterviewFeedbackResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class AIClient {

  @Value("${fastapi.base-url}")
  private String baseUrl;

  private final RestClient restClient;

  @Retryable(
      value = {HttpServerErrorException.class}, // 5xx 에러 발생했을 때만 재시도
      maxAttempts = 3,
      backoff = @Backoff(delay = 1000) // 재시도 간격
      )
  private <T> List<T> getForList(String uri, Class<T[]> responseType, Object... uriVariables) {
    log.info("FastAPI 요청 시도: {}", uri);
    T[] response =
        restClient
            .get()
            .uri(baseUrl + uri, uriVariables)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(
                status -> status.is4xxClientError(),
                (req, res) -> {
                  log.warn("FastAPI GET 실패: 상태코드 = {}", res.getStatusCode());
                  throw new CustomException(StatusCode.BAD_REQUEST);
                })
            .onStatus(
                status -> status.is5xxServerError(),
                (req, res) -> {
                  log.error("FastAPI GET 실패: 상태코드={}", res.getStatusCode());
                  throw new CustomException(StatusCode.AI_CONNECTION_FAILED);
                })
            .body(responseType);

    return Arrays.asList(response);
  }

  private <T> void postForNoContent(String uri, Object body, Object... uriVariables) {
    log.info("FastAPI 요청 시도: {}", uri);
    restClient
        .post()
        .uri(baseUrl + uri, uriVariables)
        .contentType(MediaType.APPLICATION_JSON)
        .body(body)
        .retrieve()
        .onStatus(
            status -> status.is4xxClientError(),
            (req, res) -> {
              log.warn("FastAPI POST 실패: 상태코드 = {}", res.getStatusCode());
              throw new CustomException(StatusCode.BAD_REQUEST);
            })
        .onStatus(
            status -> status.is5xxServerError(),
            (req, res) -> {
              log.error("FastAPI POST 실패: 상태코드={}", res.getStatusCode());
              throw new CustomException(StatusCode.AI_CONNECTION_FAILED);
            })
        .toBodilessEntity();
  }

  private <T> List<T> postForList(
      String uri, Object requestBody, Class<T[]> responseType, Object... uriVariables) {
    log.info("FastAPI 요청 시도: {}", uri);
    T[] response =
        restClient
            .post()
            .uri(baseUrl + uri, uriVariables)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(requestBody)
            .retrieve()
            .onStatus(
                status -> status.is4xxClientError(),
                (req, res) -> {
                  log.warn("FastAPI POST 실패: 상태코드 = {}", res.getStatusCode());
                  throw new CustomException(StatusCode.BAD_REQUEST);
                })
            .onStatus(
                status -> status.is5xxServerError(),
                (req, res) -> {
                  log.error("FastAPI POST 실패: 상태코드={}", res.getStatusCode());
                  throw new CustomException(StatusCode.AI_CONNECTION_FAILED);
                })
            .body(responseType);

    return Arrays.asList(response);
  }

  private <T> T postForObject(
      String uri, Object body, Class<T> responseType, Object... uriVariables) {
    log.info("FastAPI POST 요청 시도: {}", uri);
    return restClient
        .post()
        .uri(baseUrl + uri, uriVariables)
        .contentType(MediaType.APPLICATION_JSON)
        .body(body)
        .retrieve()
        .onStatus(
            status -> status.is4xxClientError(),
            (req, res) -> {
              log.warn("FastAPI POST 실패: 상태코드 = {}", res.getStatusCode());
              throw new CustomException(StatusCode.BAD_REQUEST);
            })
        .onStatus(
            status -> status.is5xxServerError(),
            (req, res) -> {
              log.error("FastAPI POST 실패: 상태코드={}", res.getStatusCode());
              throw new CustomException(StatusCode.AI_CONNECTION_FAILED);
            })
        .body(responseType);
  }

  /** 사용자 피드백 조회 */
  public List<FeedbackResponseDto> getFeedback(Long userId, Long subjectId) {
    return getForList(
        "/api/feedback?userId={userId}&subjectId={subjectId}",
        FeedbackResponseDto[].class,
        userId,
        subjectId);
  }

  /** 사전 평가 문제 조회 */
  public List<ExamResDto> getPreExam(Long userId, Long subjectId) {
    return getForList(
        "/api/pre/subject?user_id={userId}&subject_id={subjectId}",
        ExamResDto[].class,
        userId,
        subjectId);
  }

  /** 사후 평가 문제 조회 */
  public List<ExamResDto> getPostExam(Long userId, Long subjectId) {
    return getForList(
        "/api/post/subject?user_id={userId}&subject_id={subjectId}",
        ExamResDto[].class,
        userId,
        subjectId);
  }

  public void postPreExam(Long userId, ExamResultDto examResultDto) {
    postForNoContent("/api/pre/subject?user_id={userId}", examResultDto, userId);
  }

  public void postPostExam(Long userId, ExamResultDto examResultDto) {
    postForNoContent("/api/post/subject?user_id={userId}", examResultDto, userId);
  }

  /** 사용자 면접 피드백 요청 */
  public List<InterviewFeedbackResDto> getInterviewFeedback(
      Long userId, List<AiFeedbackReqDto> aiFeedbackReqDtoList) {

    return postForList(
        "/api/question/evaluate?user_id={userId}",
        aiFeedbackReqDtoList,
        InterviewFeedbackResDto[].class,
        userId);
  }
}

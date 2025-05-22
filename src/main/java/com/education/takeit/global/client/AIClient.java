package com.education.takeit.global.client;

import com.education.takeit.exam.dto.ExamResDto;
import com.education.takeit.exam.dto.ExamResultDto;
import com.education.takeit.feedback.dto.FeedbackResponseDto;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import java.util.Arrays;
import java.util.List;

import com.education.takeit.recommend.dto.UserContentResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@RequiredArgsConstructor
@Component
public class AIClient {

  @Value("${fastapi.base-url}")
  private String baseUrl;

  private final RestClient restClient;

  /** 사용자 피드백 조회 */
  public List<FeedbackResponseDto> getFeedback(String userId) {
    return getForList("/api/feedback?userId={userId}", FeedbackResponseDto[].class, userId);
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

  private <T> List<T> getForList(String uri, Class<T[]> responseType, Object... uriVariables) {
    T[] response =
        restClient
            .get()
            .uri(baseUrl + uri, uriVariables)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(
                status -> !status.is2xxSuccessful(),
                (req, res) -> {
                  log.warn("FastAPI 실패: 상태코드 = {}", res.getStatusCode());
                  throw new CustomException(StatusCode.AI_CONNECTION_FAILED);
                })
            .body(responseType);

    return Arrays.asList(response);
  }

  private <T> void postForNoContent(String uri, Object body, Object... uriVariables) {
    restClient
        .post()
        .uri(baseUrl + uri, uriVariables)
        .contentType(MediaType.APPLICATION_JSON)
        .body(body)
        .retrieve()
        .onStatus(
            status -> !status.is2xxSuccessful(),
            (req, res) -> {
              log.warn("FastAPI POST 실패: 상태코드 = {}", res.getStatusCode());
              throw new CustomException(StatusCode.AI_CONNECTION_FAILED);
            })
        .toBodilessEntity();
  }

  /** 추천 컨텐츠 요청 */
  public List<UserContentResDto> getRecommendation(Long userId, Long subjectId){
    return getForList(
            "/fastapi요청경로?userId={userId}&subjectId={subjectId}",
            UserContentResDto[].class,
            userId,
            subjectId
    );
  }
}

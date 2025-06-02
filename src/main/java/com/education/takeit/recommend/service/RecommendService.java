package com.education.takeit.recommend.service;

import com.education.takeit.exam.service.ExamService;
import com.education.takeit.global.client.AIClient;
import com.education.takeit.recommend.dto.UserContentResDto;
import com.education.takeit.recommend.entity.TotalContent;
import com.education.takeit.recommend.entity.UserContent;
import com.education.takeit.recommend.repository.UserContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendService {
  private final UserContentRepository userContentRepository;
  private final ExamService examService;
  private final AIClient aiClient;

  // 마이페이지에서 사용자 추천 컨텐츠 조회
  public List<UserContentResDto> getUserContent(Long userId) {
    List<UserContent> userContentsList = userContentRepository.findByUserIdWithContent(userId);

    return userContentsList.stream()
        .map(
            uc -> {
              TotalContent tc = uc.getTotalContent();
              return new UserContentResDto(
                  tc.getTotalContentId(),
                  tc.getSubject().getSubId(),
                  tc.getContentTitle(),
                  tc.getContentUrl(),
                  tc.getContentType(),
                  tc.getContentPlatform(),
                  tc.getContentDuration().name(),
                  tc.getContentPrice().name(),
                  uc.getIsAiRecommended(),
                  uc.getAiRecommendReason());
            })
        .collect(Collectors.toList());
  }

  // 사용자 과목별 추천 컨텐츠 조회
  public List<UserContentResDto> findRecommendations(Long userId, Long subjectId) {
    List<UserContent> userContentsList =
        userContentRepository.findByUser_UserIdAndSubject_SubId(userId, subjectId);
    return userContentsList.stream()
        .map(
            uc -> {
              TotalContent tc = uc.getTotalContent();
              return new UserContentResDto(
                  tc.getTotalContentId(),
                  tc.getSubject().getSubId(),
                  tc.getContentTitle(),
                  tc.getContentUrl(),
                  tc.getContentType(),
                  tc.getContentPlatform(),
                  tc.getContentDuration().name(),
                  tc.getContentPrice().name(),
                  uc.getIsAiRecommended(),
                  uc.getAiRecommendReason());
            })
        .collect(Collectors.toList());
  }

  // 추천 컨텐츠 요청
  @Async // FastAPI로 요청을 보내고 응답 받는 것은 비동기로 처리. 응답을 생성하는데 시간이 오래 걸리기 때문.
  public void fetchAndSaveRecommendation(Long userId, Long subjectId) {
      try {
          List<UserContentResDto> recommendationList = aiClient.getRecommendation(userId, subjectId);
          examService.saveUserContent(userId, recommendationList);
          log.info("추천 컨텐츠 저장 완료!!!!!!!!!!!!!!! userId  : {}, subjectId  : {}", userId, subjectId);
      } catch (Exception e) {
          // 도메인 로직 중 발생한 예외
          log.warn("추천 컨텐츠 저장 실패!!!!!!!!!!!!!!!!!! - userId: {}, subjectId: {}, reason: {}",
                  userId, subjectId, e.getMessage(), e);
      }
  }
}

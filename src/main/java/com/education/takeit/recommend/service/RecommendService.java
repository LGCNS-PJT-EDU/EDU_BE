package com.education.takeit.recommend.service;

import com.education.takeit.global.client.AIClient;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.recommend.dto.UserContentResDto;
import com.education.takeit.recommend.entity.TotalContent;
import com.education.takeit.recommend.entity.UserContent;
import com.education.takeit.recommend.repository.TotalContentRepository;
import com.education.takeit.recommend.repository.UserContentRepository;
import com.education.takeit.roadmap.entity.Subject;
import com.education.takeit.roadmap.repository.SubjectRepository;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecommendService {
  private final UserContentRepository userContentRepository;
  private final TotalContentRepository totalContentRepository;
  private final UserRepository userRepository;
  private final SubjectRepository subjectRepository;
  private final AIClient aiClient;

  // 마이페이지에서 사용자 추천 컨텐츠 조회
  public List<UserContentResDto> getUserContent(long userId) {
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
                  uc.getIsAiRecommended());
            })
        .collect(Collectors.toList());
  }

  // 추천 컨텐츠 요청
  public List<UserContentResDto> fetchAndSaveRecommendation(Long userId, Long subjectId) {
    List<UserContentResDto> recommendationList = aiClient.getRecommendation(userId, subjectId);
    saveUserContent(userId, recommendationList);
    return recommendationList;
  }

  // DB에 저장
  private void saveUserContent(Long userId, List<UserContentResDto> userContentList) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new CustomException(StatusCode.NOT_EXIST_USER));

    List<UserContent> contentList =
        userContentList.stream()
            .map(
                dto -> {
                  TotalContent totalContent =
                      totalContentRepository
                          .findById(dto.totalContentId())
                          .orElseThrow(() -> new CustomException(StatusCode.CONTENTS_NOT_FOUND));
                  Subject subject =
                      subjectRepository
                          .findById(dto.subjectId())
                          .orElseThrow(() -> new CustomException(StatusCode.SUBJECT_NOT_FOUND));
                  return new UserContent(null, totalContent, subject, user, dto.isAiRecommended());
                })
            .toList();

    userContentRepository.saveAll(contentList);
  }
}

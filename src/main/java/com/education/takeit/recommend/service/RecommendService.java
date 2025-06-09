package com.education.takeit.recommend.service;

import com.education.takeit.exam.service.ExamService;
import com.education.takeit.global.client.AIClient;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.kafka.recommand.dto.RecomResultDto;
import com.education.takeit.recommend.dto.UserContentResDto;
import com.education.takeit.recommend.entity.TotalContent;
import com.education.takeit.recommend.entity.UserContent;
import com.education.takeit.recommend.repository.TotalContentRepository;
import com.education.takeit.recommend.repository.UserContentRepository;
import com.education.takeit.roadmap.entity.Subject;
import com.education.takeit.roadmap.repository.SubjectRepository;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendService {
  private final UserContentRepository userContentRepository;
  private final UserRepository userRepository;
  private final SubjectRepository subjectRepository;
  private final TotalContentRepository totalContentRepository;
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

  public void saveUserContents(RecomResultDto dto) {
    User user =
        userRepository
            .findByUserId(dto.userId())
            .orElseThrow(() -> new CustomException(StatusCode.USER_NOT_FOUND));
    Subject subject =
        subjectRepository
            .findBySubId(dto.subjectId())
            .orElseThrow(() -> new CustomException(StatusCode.NOT_EXIST_SUBJECT));

    List<UserContent> recommendations =
        dto.recommendation().stream()
            .map(
                userContentResDto -> {
                  TotalContent totalContent =
                      totalContentRepository
                          .findById(userContentResDto.contentId())
                          .orElseThrow(() -> new CustomException(StatusCode.CONTENTS_NOT_FOUND));
                  return UserContent.builder()
                      .totalContent(totalContent)
                      .subject(subject)
                      .user(user)
                      .isAiRecommended(userContentResDto.isAiRecommendation())
                      .aiRecommendReason(userContentResDto.comment())
                      .build();
                })
            .toList();
    userContentRepository.saveAll(recommendations);
  }
}

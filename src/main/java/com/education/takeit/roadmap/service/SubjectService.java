package com.education.takeit.roadmap.service;

import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.recommend.dto.UserContentResDto;
import com.education.takeit.recommend.service.RecommendService;
import com.education.takeit.roadmap.dto.ChapterFindDto;
import com.education.takeit.roadmap.dto.SubjectFindResDto;
import com.education.takeit.roadmap.entity.Roadmap;
import com.education.takeit.roadmap.entity.RoadmapManagement;
import com.education.takeit.roadmap.entity.Subject;
import com.education.takeit.roadmap.repository.ChapterRepository;
import com.education.takeit.roadmap.repository.RoadmapManagementRepository;
import com.education.takeit.roadmap.repository.RoadmapRepository;
import com.education.takeit.roadmap.repository.SubjectRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubjectService {
  private final SubjectRepository subjectRepository;
  private final ChapterRepository chapterRepository;
  private final RoadmapRepository roadmapRepository;
  private final RoadmapManagementRepository roadmapManagementRepository;
  private final RecommendService recommendService;

  public SubjectFindResDto findUserSubject(Long userId, Long roadmapId) {
    if (userId == null) {
      throw new CustomException(StatusCode.USER_NOT_FOUND);
    }
    // roadmapId 로 roadmap 정보 조회
    Roadmap roadmap = roadmapRepository.findByRoadmapId(roadmapId);

    // subjectId 로 subject 정보 조회
    Subject subject = roadmap.getSubject();

    // chapter 정보 조회
    List<ChapterFindDto> chapters =
        chapterRepository.findBySubject(subject).stream()
            .map(ch -> new ChapterFindDto(ch.getChapterName(), ch.getChapterOrder()))
            .toList();

    RoadmapManagement userRoadmapManagement = roadmapManagementRepository.findByUserId(userId);
    if (userRoadmapManagement == null) {
      throw new CustomException(StatusCode.ROADMAP_NOT_FOUND);
    }
    Roadmap userRoadmap =
        roadmapRepository.findBySubjectAndRoadmapManagement(subject, userRoadmapManagement);

    // 추천 컨텐츠 받아오기
    List<UserContentResDto> recommendContents =
        recommendService.findRecommendations(userId,subject.getSubId());

    // DTO화
    return new SubjectFindResDto(
        subject.getSubNm(),
        subject.getSubOverview(),
        chapters,
        userRoadmap.getPreSubmitCount(),
        userRoadmap.getPostSubmitCount(),
        recommendContents);
  }
}

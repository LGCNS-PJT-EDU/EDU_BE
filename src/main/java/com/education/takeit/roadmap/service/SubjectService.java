package com.education.takeit.roadmap.service;

import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.roadmap.dto.ChapterFindDto;
import com.education.takeit.roadmap.dto.RecommendContentsFindDto;
import com.education.takeit.roadmap.dto.SubjectFindResDto;
import com.education.takeit.roadmap.entity.Roadmap;
import com.education.takeit.roadmap.entity.Subject;
import com.education.takeit.roadmap.repository.ChapterRepository;
import com.education.takeit.roadmap.repository.RoadmapRepository;
import com.education.takeit.roadmap.repository.SubjectRepository;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubjectService {
  private final SubjectRepository subjectRepository;
  private final ChapterRepository chapterRepository;
  private final RoadmapRepository roadmapRepository;

  public List<RecommendContentsFindDto> findRecommendContents() {
    RecommendContentsFindDto dummy1 = new RecommendContentsFindDto("추천 컨텐츠 제공 예정", "url1", "동영상");
    RecommendContentsFindDto dummy2 = new RecommendContentsFindDto("추천 컨텐츠 제공 예정", "url2", "동영상");
    RecommendContentsFindDto dummy3 = new RecommendContentsFindDto("추천 컨텐츠 제공 예정", "url3", "책");

    return Arrays.asList(dummy1, dummy2, dummy3);
  }

  public SubjectFindResDto findUserSubject(Long userId, Long subjectId) {
    if (userId == null) {
      throw new CustomException(StatusCode.USER_NOT_FOUND);
    }

    // subjectId 로 subject 정보 조회
    Subject subject =
        subjectRepository
            .findById(subjectId)
            .orElseThrow(() -> new CustomException(StatusCode.SUBJECT_NOT_FOUND));

    // chapter 정보 조회
    List<ChapterFindDto> chapters =
        chapterRepository.findBySubject(subject).stream()
            .map(ch -> new ChapterFindDto(ch.getChapterName(), ch.getChapterOrder()))
            .toList();

    // 사용자의 과목과 관련된 정보 조회
    Roadmap userRoadmap = roadmapRepository.findByUserIdAndSubject_SubId(userId, subjectId);

    // 추천 컨텐츠 받아오기(임시)
    List<RecommendContentsFindDto> recommendContents = findRecommendContents();

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

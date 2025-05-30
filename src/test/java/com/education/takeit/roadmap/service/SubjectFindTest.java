package com.education.takeit.roadmap.service;

import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.recommend.dto.UserContentResDto;
import com.education.takeit.recommend.service.RecommendService;
import com.education.takeit.roadmap.dto.SubjectFindResDto;
import com.education.takeit.roadmap.entity.Chapter;
import com.education.takeit.roadmap.entity.Roadmap;
import com.education.takeit.roadmap.entity.RoadmapManagement;
import com.education.takeit.roadmap.entity.Subject;
import com.education.takeit.roadmap.repository.ChapterRepository;
import com.education.takeit.roadmap.repository.RoadmapManagementRepository;
import com.education.takeit.roadmap.repository.RoadmapRepository;
import com.education.takeit.roadmap.repository.SubjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubjectFindTest {
  @InjectMocks private SubjectService subjectService;

  @Mock private SubjectRepository subjectRepository;

  @Mock private ChapterRepository chapterRepository;

  @Mock private RoadmapManagementRepository roadmapManagementRepository;

  @Mock private RoadmapRepository roadmapRepository;

  @Mock private RecommendService recommendService;

  @Test
  void findUserSubject_shouldReturnSubjectInfo_whenValidInput() {
    // given
    Long userId = 1L;
    Long subjectId = 35L;
    Long roadmapId = 100L;

    Subject subject =
        Subject.builder()
            .subId(subjectId)
            .subNm("Linux")
            .subOverview(
                "리눅스 명령어란? 터미널에서 직접 컴퓨터를 조작할 수 있게 해주는 텍스트 기반의 도구! 파일 생성, 복사, 이동부터 시스템 관리, 프로세스 확인, 서버 설정까지 명령어 한 줄로 다양한 작업을 빠르고 정확하게 수행할 수 있어요. 예를 들면, ls로 폴더 목록을 확인하고, cd로 위치를 옮기거나, chmod, ps, top 같은 명령어로 권한과 시스템 상태를 관리할 수 있어요. 마치 명령 센터에서 키워드 하나로 기계를 제어하듯, 개발과 운영 환경에서 효율적이고 강력한 제어력을 제공해주는 기본 도구예요.")
            .subType("BE")
            .subEssential("Y")
            .baseSubOrder(1)
            .track(null)
            .build();

    Chapter chapter1 =
        Chapter.builder().chapterName("기본 명령어와 파일 시스템").chapterOrder(1).subject(subject).build();

    Chapter chapter2 =
        Chapter.builder().chapterName("사용자와 권한 관리").chapterOrder(2).subject(subject).build();

    Chapter chapter3 =
        Chapter.builder().chapterName("프로세스와 작업 관리").chapterOrder(3).subject(subject).build();

    Chapter chapter4 =
        Chapter.builder().chapterName("패키지 및 소프트웨어 설치").chapterOrder(4).subject(subject).build();

    Chapter chapter5 =
        Chapter.builder().chapterName("시스템 관리와 로그 분석").chapterOrder(5).subject(subject).build();

    RoadmapManagement roadmapManagement =
        RoadmapManagement.builder().roadmapManagementId(100L).userId(userId).build();

    Roadmap roadmap =
        Roadmap.builder()
            .roadmapId(roadmapId)
            .subject(subject)
            .roadmapManagement(roadmapManagement)
            .preSubmitCount(1)
            .postSubmitCount(3)
            .build();

    List<UserContentResDto> mockContents = List.of(); // 추천컨텐츠 받을 리스트

    when(roadmapRepository.findByRoadmapId(roadmapId)).thenReturn(roadmap);
    //    when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));
    when(chapterRepository.findBySubject(subject))
        .thenReturn(List.of(chapter1, chapter2, chapter3, chapter4, chapter5));
    when(roadmapManagementRepository.findByUserId(userId)).thenReturn(roadmapManagement);
    when(roadmapRepository.findBySubjectAndRoadmapManagement(subject, roadmapManagement))
        .thenReturn(roadmap);
    when(recommendService.fetchAndSaveRecommendation(userId, subjectId)).thenReturn(mockContents);

    // when
    SubjectFindResDto result = subjectService.findUserSubject(userId, roadmapId);

    // then
    assertEquals("Linux", result.subject_name());
    assertEquals(
        "리눅스 명령어란? 터미널에서 직접 컴퓨터를 조작할 수 있게 해주는 텍스트 기반의 도구! 파일 생성, 복사, 이동부터 시스템 관리, 프로세스 확인, 서버 설정까지 명령어 한 줄로 다양한 작업을 빠르고 정확하게 수행할 수 있어요. 예를 들면, ls로 폴더 목록을 확인하고, cd로 위치를 옮기거나, chmod, ps, top 같은 명령어로 권한과 시스템 상태를 관리할 수 있어요. 마치 명령 센터에서 키워드 하나로 기계를 제어하듯, 개발과 운영 환경에서 효율적이고 강력한 제어력을 제공해주는 기본 도구예요.",
        result.subject_overview());
    assertEquals(5, result.chapters().size());
    assertEquals("기본 명령어와 파일 시스템", result.chapters().get(0).chapterName());
    assertEquals("프로세스와 작업 관리", result.chapters().get(2).chapterName());
    assertEquals(1, result.preSubmitCount());
    assertEquals(3, result.postSubmitCount());
    assertEquals(mockContents, result.recommendContents());
  }

  @Test
  void findUserSubject_shouldThrow_whenUserIdIsNull() {
    // when & then
    CustomException ex =
        assertThrows(CustomException.class, () -> subjectService.findUserSubject(null, 10L));

    assertEquals(StatusCode.USER_NOT_FOUND, ex.getStatusCode());
  }


  // 과목 상세 정보를 조회할때 roadmapId를 전달받기 때문에 아래의 테스트 코드는 더 이상 맞지 않습니다.

//  @Test
//  void findUserSubject_shouldThrow_whenSubjectNotFound() {
//    // given
//    Long userId = 1L;
//    Long subjectId = 99L;
//    when(subjectRepository.findById(subjectId)).thenReturn(Optional.empty());
//
//    // when & then
//    CustomException ex =
//        assertThrows(
//            CustomException.class, () -> subjectService.findUserSubject(userId, subjectId));
//
//    assertEquals(StatusCode.SUBJECT_NOT_FOUND, ex.getStatusCode());
//  }
//
//  @Test
//  void findUserSubject_shouldThrow_whenUserRoadmapManagementNotFound() {
//    // given
//    long userId = 1L;
//    Long subjectId = 10L;
//
//    Subject subject = Subject.builder().subId(subjectId).subNm("Java").build();
//
//    when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));
//    when(roadmapManagementRepository.findByUserId(userId)).thenReturn(null);
//
//    // when & then
//    CustomException ex =
//        assertThrows(
//            CustomException.class, () -> subjectService.findUserSubject(userId, subjectId));
//
//    assertEquals(StatusCode.ROADMAP_NOT_FOUND, ex.getStatusCode());
//  }
}

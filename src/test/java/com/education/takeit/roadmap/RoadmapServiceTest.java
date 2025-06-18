package com.education.takeit.roadmap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

import com.education.takeit.diagnosis.dto.DiagnosisAnswerRequest;
import com.education.takeit.feedback.repository.FeedbackRepository;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.recommend.dto.UserContentResDto;
import com.education.takeit.recommend.repository.UserContentRepository;
import com.education.takeit.recommend.service.RecommendService;
import com.education.takeit.roadmap.dto.*;
import com.education.takeit.roadmap.entity.*;
import com.education.takeit.roadmap.repository.ChapterRepository;
import com.education.takeit.roadmap.repository.RoadmapManagementRepository;
import com.education.takeit.roadmap.repository.RoadmapRepository;
import com.education.takeit.roadmap.repository.SubjectRepository;
import com.education.takeit.roadmap.service.RoadmapService;
import com.education.takeit.roadmap.service.RoadmapTransactionalService;
import com.education.takeit.roadmap.service.SubjectService;
import com.education.takeit.solution.repository.UserExamAnswerRepository;
import com.education.takeit.user.entity.LectureAmount;
import com.education.takeit.user.entity.LoginType;
import com.education.takeit.user.entity.PriceLevel;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
public class RoadmapServiceTest {
  @Spy @InjectMocks RoadmapService roadmapService;
  @InjectMocks SubjectService subjectService;
  @Mock RecommendService recommendService;
  @Mock private RedisTemplate<String, String> redisTemplate;
  @Mock private ValueOperations<String, String> valueOperations;
  @Mock private ObjectMapper objectMapper;
  @Mock private RoadmapRepository roadmapRepository;
  @Mock private RoadmapManagementRepository roadmapManagementRepository;
  @Mock private RoadmapTransactionalService roadmapTransactionalService;
  @InjectMocks private RoadmapTransactionalService injectedRoadmapTransactionalService;
  @Mock private SubjectRepository subjectRepository;
  @Mock private ChapterRepository chapterRepository;
  @Mock private UserRepository userRepository;
  @Mock private UserContentRepository userContentRepository;
  @Mock private FeedbackRepository feedbackRepository;
  @Mock private UserExamAnswerRepository userExamAnswerRepository;

  private List<Roadmap> FE_roadmapList;
  private List<Roadmap> BE_roadmapList;
  private List<DiagnosisAnswerRequest> answers;
  private User user;

  @BeforeEach
  void setUp() {
    Subject subject = new Subject();

    RoadmapManagement FE_roadmapManagement =
        RoadmapManagement.builder()
            .roadmapManagementId(1L)
            .roadmapNm("FE_DEFAULT_ROADMAP")
            .roadmapTimestamp(LocalDateTime.now())
            .userId(1L)
            .build();

    RoadmapManagement BE_roadmapManagement =
        RoadmapManagement.builder()
            .roadmapManagementId(2L)
            .roadmapNm("BE_DEFAULT_ROADMAP")
            .roadmapTimestamp(LocalDateTime.now())
            .userId(2L)
            .build();

    // FE 로드맵 데이터 (roadmap_management_id == 1)
    FE_roadmapList =
        List.of(
            Roadmap.builder()
                .roadmapId(1L)
                .orderSub(1)
                .subject(subject)
                .roadmapManagement(FE_roadmapManagement)
                .build(),
            Roadmap.builder()
                .roadmapId(2L)
                .orderSub(2)
                .subject(subject)
                .roadmapManagement(FE_roadmapManagement)
                .build(),
            Roadmap.builder()
                .roadmapId(3L)
                .orderSub(3)
                .subject(subject)
                .roadmapManagement(FE_roadmapManagement)
                .build(),
            // ... 생략
            Roadmap.builder()
                .roadmapId(26L)
                .orderSub(26)
                .subject(subject)
                .roadmapManagement(FE_roadmapManagement)
                .build());

    // BE 로드맵 데이터 (roadmap_management_id == 2)
    BE_roadmapList =
        List.of(
            Roadmap.builder()
                .roadmapId(27L)
                .orderSub(1)
                .subject(subject)
                .roadmapManagement(BE_roadmapManagement)
                .build(),
            Roadmap.builder()
                .roadmapId(28L)
                .orderSub(2)
                .subject(subject)
                .roadmapManagement(BE_roadmapManagement)
                .build(),
            Roadmap.builder()
                .roadmapId(29L)
                .orderSub(3)
                .subject(subject)
                .roadmapManagement(BE_roadmapManagement)
                .build(),
            // ... 생략
            Roadmap.builder()
                .roadmapId(37L)
                .orderSub(11)
                .subject(subject)
                .roadmapManagement(BE_roadmapManagement)
                .build());

    answers =
        List.of(
            new DiagnosisAnswerRequest(1L, "FE"),
            new DiagnosisAnswerRequest(2L, "2"),
            new DiagnosisAnswerRequest(3L, "2"),
            new DiagnosisAnswerRequest(4L, "Y"),
            new DiagnosisAnswerRequest(5L, "React"),
            new DiagnosisAnswerRequest(6L, "Y"),
            new DiagnosisAnswerRequest(7L, "Y"),
            new DiagnosisAnswerRequest(8L, "N"),
            new DiagnosisAnswerRequest(9L, "Y"),
            new DiagnosisAnswerRequest(10L, "N"));

    lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);

    user =
        User.builder().email("test@test.com").loginType(LoginType.LOCAL).nickname("test").build();
  }

  @Test
  @DisplayName("게스트 사용자는 Redis에 임시 로드맵 데이터를 저장하고 UUID 포함한 응답 반환")
  void 비회원의_진단_결과로_로드맵_생성() throws JsonProcessingException {
    // given
    List<DiagnosisAnswerRequest> answers = List.of(new DiagnosisAnswerRequest(1L, "FE"));

    List<Subject> essentialSubjects =
        List.of(
            new Subject(1L, "HTML", "FE", "Y", 1, "", null),
            new Subject(2L, "CSS", "FE", "Y", 2, "", null));

    when(subjectRepository.findBySubTypeAndSubEssential("FE", "Y")).thenReturn(essentialSubjects);
    when(objectMapper.writeValueAsString(answers))
        .thenReturn("[{\"questionId\":1,\"answer\":\"FE\"}]");

    // when
    RoadmapSaveResDto result = roadmapService.selectRoadmap(null, answers);

    // then
    assertThat(result.uuid()).isNotNull();
    assertThat(result.subjects()).hasSize(2);
    assertThat(result.subjects().getFirst().subjectName()).isEqualTo("HTML");

    // Redis 저장 확인
    verify(redisTemplate, times(2)).opsForValue();
    verify(valueOperations)
        .set(matches("guest:.*:subjects"), anyString(), eq(Duration.ofMinutes(15)));
    verify(valueOperations)
        .set(matches("guest:.*:answers"), anyString(), eq(Duration.ofMinutes(15)));
  }

  @Test
  @DisplayName("createRoadmap - BE 트랙 선택 시 defaultLocationSubjectId는 35")
  void createRoadmap_BE기본위치확인() {
    List<DiagnosisAnswerRequest> answers = List.of(new DiagnosisAnswerRequest(1L, "BE"));
    when(subjectRepository.findBySubTypeAndSubEssential("BE", "Y")).thenReturn(List.of());

    RoadmapSaveResDto result = roadmapService.createRoadmap(answers);

    assertThat(result.userLocationSubjectId()).isEqualTo(35L);
  }

  @Test
  @DisplayName("createRoadmap - 조건 분기 포함한 생성 (React + Java/Spring + Y)")
  void createRoadmap_조건포함_분기확인() {
    // Given
    List<DiagnosisAnswerRequest> answers =
        List.of(
            new DiagnosisAnswerRequest(1L, "FE"), // 트랙
            new DiagnosisAnswerRequest(5L, "React"), // React 과목들
            new DiagnosisAnswerRequest(11L, "Java/Spring"), // 백엔드 분기
            new DiagnosisAnswerRequest(14L, "Y"),
            new DiagnosisAnswerRequest(15L, "Y"));

    when(subjectRepository.findBySubTypeAndSubEssential("FE", "Y")).thenReturn(List.of());

    // React 관련
    when(subjectRepository.findById(10L))
        .thenReturn(Optional.of(new Subject(10L, "React", "FE", "N", 1, "", null)));
    when(subjectRepository.findById(11L))
        .thenReturn(Optional.of(new Subject(11L, "Redux", "FE", "N", 2, "", null)));
    when(subjectRepository.findById(12L))
        .thenReturn(Optional.of(new Subject(12L, "Zustand", "FE", "N", 3, "", null)));
    when(subjectRepository.findById(22L))
        .thenReturn(Optional.of(new Subject(22L, "Next.js", "FE", "N", 4, "", null)));
    when(subjectRepository.findById(23L))
        .thenReturn(Optional.of(new Subject(23L, "React 랜더링", "FE", "N", 5, "", null)));
    when(subjectRepository.findById(24L))
        .thenReturn(Optional.of(new Subject(24L, "React Query", "FE", "N", 6, "", null)));

    // Java/Spring 관련
    when(subjectRepository.findById(39L))
        .thenReturn(Optional.of(new Subject(39L, "Java", "BE", "N", 7, "", null)));
    when(subjectRepository.findById(46L))
        .thenReturn(Optional.of(new Subject(46L, "Spring", "BE", "N", 8, "", null)));

    // Q14: flag == 1 → 51L
    when(subjectRepository.findById(51L))
        .thenReturn(Optional.of(new Subject(51L, "추가Q14", "BE", "N", 9, "", null)));
    // Q15: flag == 1 → 53L
    when(subjectRepository.findById(53L))
        .thenReturn(Optional.of(new Subject(53L, "추가Q15", "BE", "N", 10, "", null)));

    // When
    RoadmapSaveResDto result = roadmapService.createRoadmap(answers);

    // Then
    List<Long> actualIds = result.subjects().stream().map(SubjectDto::subjectId).toList();
    assertThat(actualIds).contains(10L, 11L, 12L, 22L, 23L, 24L, 39L, 46L, 51L, 53L);
  }

  @Test
  @DisplayName("로그인 사용자는 기존 로드맵이 있으면 삭제하고 새로운 로드맵을 저장한다")
  void 회원의_진단_결과로_로드맵_생성() {
    // given
    Long userId = 1L;

    List<DiagnosisAnswerRequest> answers = List.of(new DiagnosisAnswerRequest(1L, "FE"));

    List<SubjectDto> subjects =
        List.of(new SubjectDto(1L, "HTML", 1), new SubjectDto(2L, "CSS", 2));

    RoadmapSaveResDto roadmapSaveResDto = new RoadmapSaveResDto(null, "로드맵이름", 1L, subjects);

    RoadmapManagement existingRoadmapManagement = mock(RoadmapManagement.class);

    // roadmapManagementRepository.findByUserId → 기존 로드맵 존재
    when(roadmapManagementRepository.findByUserId(userId)).thenReturn(existingRoadmapManagement);

    // 내부 호출될 메서드 mocking
    // createRoadmap은 실제 로직을 쓰거나 spy로 감싸도 됩니다
    doReturn(roadmapSaveResDto).when(roadmapService).createRoadmap(answers);

    // saveRoadmap은 단순히 호출 여부만 검증
    doNothing().when(roadmapService).saveRoadmap(anyLong(), anyString(), anyList(), anyList());

    // when
    RoadmapSaveResDto result = roadmapService.selectRoadmap(userId, answers);

    // then
    assertThat(result).isNotNull();
    assertThat(result.subjects()).hasSize(2);
    assertThat(result.subjects().getFirst().subjectName()).isEqualTo("HTML");

    verify(roadmapTransactionalService, times(1)).deleteRoadmap(userId);
    verify(roadmapService).saveRoadmap(eq(userId), anyString(), anyList(), eq(answers));
  }

  @Test
  @DisplayName("로드맵 생성")
  void 로드맵_생성() {
    // Given
    Track track = new Track();

    // 필수 과목
    when(subjectRepository.findBySubTypeAndSubEssential("FE", "Y"))
        .thenReturn(
            List.of(
                new Subject(1L, "HTML", "FE", "Y", 1, "", track),
                new Subject(2L, "CSS", "FE", "Y", 2, "", track),
                new Subject(3L, "JavaScript", "FE", "Y", 3, "", track),
                new Subject(4L, "TypeScript", "FE", "Y", 4, "", track),
                new Subject(5L, "Virtual DOM", "FE", "Y", 5, "", track),
                new Subject(6L, "Git & GitHub", "FE", "Y", 6, "", track),
                new Subject(7L, "Git Hook 자동화", "FE", "Y", 7, "", track),
                new Subject(8L, "Axios 자동화", "FE", "Y", 8, "", track),
                new Subject(9L, "REST API", "FE", "Y", 9, "", track),
                new Subject(29L, "Webpack 개념과 설정", "FE", "Y", 29, "", track)));

    // React 과목들
    when(subjectRepository.findById(10L))
        .thenReturn(Optional.of(new Subject(10L, "React", "FE", "N", 3, "", track)));
    when(subjectRepository.findById(11L))
        .thenReturn(Optional.of(new Subject(11L, "Redux", "FE", "N", 4, "", track)));
    when(subjectRepository.findById(12L))
        .thenReturn(Optional.of(new Subject(12L, "Zustand", "FE", "N", 5, "", track)));
    when(subjectRepository.findById(22L))
        .thenReturn(Optional.of(new Subject(22L, "Next.js", "FE", "N", 6, "", track)));
    when(subjectRepository.findById(23L))
        .thenReturn(Optional.of(new Subject(23L, "React 랜더링", "FE", "N", 7, "", track)));
    when(subjectRepository.findById(24L))
        .thenReturn(Optional.of(new Subject(24L, "React Query 심화", "FE", "N", 8, "", track)));

    // 기타 조건 과목들
    when(subjectRepository.findById(21L))
        .thenReturn(Optional.of(new Subject(21L, "EsLint & Prettier", "FE", "N", 9, "", track)));
    when(subjectRepository.findById(17L))
        .thenReturn(Optional.of(new Subject(17L, "Storybook", "FE", "N", 10, "", track)));
    when(subjectRepository.findById(18L))
        .thenReturn(Optional.of(new Subject(18L, "Tailwind CSS", "FE", "N", 11, "", track)));
    when(subjectRepository.findById(19L))
        .thenReturn(Optional.of(new Subject(19L, "SCSS", "FE", "N", 12, "", track)));
    when(subjectRepository.findById(20L))
        .thenReturn(Optional.of(new Subject(20L, "styled", "FE", "N", 13, "", track)));
    when(subjectRepository.findById(33L))
        .thenReturn(Optional.of(new Subject(33L, "Component Test", "FE", "N", 14, "", track)));

    // When
    RoadmapSaveResDto result = roadmapService.createRoadmap(answers);
    assertThat(result).isNotNull();
    // Then
    List<Long> actualSubjectIds = result.subjects().stream().map(SubjectDto::subjectId).toList();

    List<Long> expectedSubjectIds =
        List.of(
            1L, 2L, 3L, 10L, 4L, 11L, 5L, 12L, 6L, 22L, 7L, 23L, 8L, 24L, 9L, 21L, 17L, 18L, 19L,
            20L, 33L, 29L);

    assertThat(actualSubjectIds).containsExactlyElementsOf(expectedSubjectIds);
    assertThat(result.userLocationSubjectId()).isEqualTo(1L); // FE일 경우 기본 ID
  }

  @Test
  @DisplayName("정상적인 로드맵 저장")
  void 사용자_로드맵_저장() throws JsonProcessingException {
    // Given
    Long userId = 1L;
    List<Long> subjectIds = List.of(1L, 2L);
    List<DiagnosisAnswerRequest> answers =
        List.of(
            new DiagnosisAnswerRequest(2L, "1"), // LectureAmount[1]
            new DiagnosisAnswerRequest(3L, "2"), // PriceLevel[2]
            new DiagnosisAnswerRequest(4L, "Y") // likesBooks = true
            );

    // 기존 로드맵 없음
    when(roadmapManagementRepository.findByUserId(userId)).thenReturn(null);

    // 사용자 모킹
    User user = mock(User.class);
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    // 과목 목록
    Subject subject1 = new Subject(1L, "HTML", "FE", "Y", 1, "", null);
    Subject subject2 = new Subject(2L, "CSS", "FE", "Y", 2, "", null);
    when(subjectRepository.findAllById(subjectIds)).thenReturn(List.of(subject1, subject2));

    // When
    roadmapService.saveRoadmap(userId, "테스트용 로드맵", subjectIds, answers);

    // Then
    verify(roadmapManagementRepository).save(any(RoadmapManagement.class));
    verify(user)
        .updatePreferences(eq(LectureAmount.values()[1]), eq(PriceLevel.values()[2]), eq(true));
    verify(roadmapRepository)
        .saveAll(
            argThat(
                roadmaps -> {
                  List<Roadmap> list = StreamSupport.stream(roadmaps.spliterator(), false).toList();
                  return list.size() == 2
                      && list.get(0).getSubject().getSubId().equals(1L)
                      && list.get(1).getSubject().getSubId().equals(2L);
                }));
  }

  @Test
  @DisplayName("레디스에 저장된 비회원의 로드맵을 uuid로 찾아서 DB에 저장")
  void 게스트_로드맵_저장() throws JsonProcessingException {
    // Given
    String uuid = "abc-uuid-123";
    Long userId = 100L;

    String redisSubjectIds = "1,2,3";
    String redisAnswersJson =
        """
        [
            {"questionId":2,"answer":"1"},
            {"questionId":3,"answer":"2"},
            {"questionId":4,"answer":"Y"}
        ]
    """;

    List<Long> subjectIds = List.of(1L, 2L, 3L);
    List<DiagnosisAnswerRequest> parsedAnswers =
        List.of(
            new DiagnosisAnswerRequest(2L, "1"),
            new DiagnosisAnswerRequest(3L, "2"),
            new DiagnosisAnswerRequest(4L, "Y"));

    List<Subject> subjects =
        List.of(
            new Subject(1L, "HTML", "FE", "Y", 1, "", null),
            new Subject(2L, "CSS", "FE", "Y", 2, "", null),
            new Subject(3L, "JavaScript", "FE", "Y", 3, "", null));

    TypeReference<List<DiagnosisAnswerRequest>> typeRef = new TypeReference<>() {};

    when(redisTemplate.opsForValue().get("guest:" + uuid + ":subjects"))
        .thenReturn(redisSubjectIds);
    when(redisTemplate.opsForValue().get("guest:" + uuid + ":answers"))
        .thenReturn(redisAnswersJson);
    lenient()
        .when(objectMapper.readValue(anyString(), any(TypeReference.class)))
        .thenReturn(parsedAnswers);
    when(subjectRepository.findAllById(subjectIds)).thenReturn(subjects);
    doNothing().when(roadmapService).saveRoadmap(userId, "게스트 로드맵", subjectIds, parsedAnswers);

    // When
    RoadmapSaveResDto result = roadmapService.saveGuestRoadmap(uuid, userId);

    // Then
    assertThat(result.subjects()).hasSize(3);
    assertThat(result.userLocationSubjectId()).isEqualTo(1L);
    assertThat(result.uuid()).isEqualTo("uuid로 로드맵 생성 완료");

    verify(redisTemplate).delete("guest:" + uuid + ":subjects");
    verify(redisTemplate).delete("guest:" + uuid + ":answers");
  }

  @Test
  @DisplayName("로드맵 진척도 조회")
  void 로드맵_진척도_조회() {
    // Given
    RoadmapManagement roadmapManagement =
        RoadmapManagement.builder()
            .roadmapManagementId(10L)
            .roadmapNm("FE 로드맵")
            .userId(user.getUserId())
            .build();

    List<Roadmap> roadmaps =
        List.of(
            Roadmap.builder().isComplete(true).build(),
            Roadmap.builder().isComplete(false).build(),
            Roadmap.builder().isComplete(true).build());

    when(userRepository.findByUserId(2L)).thenReturn(Optional.of(user));
    when(roadmapManagementRepository.findByUserId(2L)).thenReturn(roadmapManagement);
    when(roadmapRepository.findByRoadmapManagement_RoadmapManagementId(10L)).thenReturn(roadmaps);

    // When
    MyPageResDto result = roadmapService.getProgressPercentage(2L);

    // Then
    assertThat(result.nickname()).isEqualTo("test");
    assertThat(result.roadmapName()).isEqualTo("FE 로드맵");
    assertThat(result.percent()).isEqualTo(66); // (2/3)*100 = 66
  }

  @Test
  @DisplayName("로드맵 수정")
  void 로드맵_수정() {
    // Given
    Long userId = 1L;
    Long roadmapManagementId = 100L;

    RoadmapManagement roadmapManagement =
        RoadmapManagement.builder()
            .roadmapManagementId(roadmapManagementId)
            .userId(userId)
            .roadmapNm("기존 로드맵")
            .roadmapTimestamp(LocalDateTime.now().minusDays(1))
            .build();

    Subject subject1 = new Subject(1L, "HTML", "FE", "Y", 1, "", null);
    Subject subject2 = new Subject(2L, "CSS", "FE", "Y", 2, "", null);
    Subject subject3 = new Subject(3L, "JavaScript", "FE", "Y", 3, "", null); // 신규 추가용

    Roadmap roadmap1 =
        Roadmap.builder()
            .subject(subject1)
            .orderSub(1)
            .roadmapManagement(roadmapManagement)
            .isComplete(true)
            .build();

    Roadmap roadmap2 =
        Roadmap.builder()
            .subject(subject2)
            .orderSub(2)
            .roadmapManagement(roadmapManagement)
            .isComplete(false)
            .build();

    when(roadmapManagementRepository.findByUserId(userId)).thenReturn(roadmapManagement);
    when(roadmapRepository.findByRoadmapManagement_RoadmapManagementId(roadmapManagementId))
        .thenReturn(List.of(roadmap1, roadmap2));

    // subject1은 순서만 변경, subject3은 신규 추가
    List<SubjectDto> updatedSubjects =
        List.of(new SubjectDto(1L, "HTML", 10), new SubjectDto(3L, "JavaScript", 11));

    when(subjectRepository.findById(3L)).thenReturn(Optional.of(subject3));

    // When
    roadmapService.updateRoadmap(userId, updatedSubjects);

    // Then
    verify(roadmapManagementRepository).save(any(RoadmapManagement.class));

    // subject2는 제거되어야 함
    verify(roadmapRepository)
        .deleteAll(
            argThat(
                toDelete -> {
                  List<Roadmap> list =
                      (List<Roadmap>) StreamSupport.stream(toDelete.spliterator(), false).toList();
                  return list.size() == 1 && list.get(0).getSubject().getSubId().equals(2L);
                }));

    verify(roadmapRepository)
        .saveAll(
            argThat(
                toSave -> {
                  List<Roadmap> list = StreamSupport.stream(toSave.spliterator(), false).toList();
                  return list.size() == 2
                      && list.stream()
                          .anyMatch(
                              r -> r.getSubject().getSubId().equals(1L) && r.getOrderSub() == 10)
                      && list.stream()
                          .anyMatch(
                              r -> r.getSubject().getSubId().equals(3L) && r.getOrderSub() == 11);
                }));
  }

  @Test
  @DisplayName("기본 FE 로드맵을 정상적으로 조회한다")
  void 기본_FE_로드맵_조회() {
    // Given
    when(roadmapRepository.findByRoadmapManagement_RoadmapManagementId(1L))
        .thenReturn(FE_roadmapList);

    // Spy 또는 doReturn 설정 (userLocationSubjectId를 1L로 고정)
    doReturn(1L).when(roadmapService).findUserLocationRoadmap(FE_roadmapList);

    // When
    RoadmapFindResDto result = roadmapService.getDefaultRoadmap("FE");

    // Then
    assertThat(result).isNotNull();
    assertThat(result.roadmapName()).isEqualTo("FE_DEFAULT_ROADMAP");
    assertThat(result.subjects()).hasSize(4);
    assertThat(result.userLocationSubjectId()).isEqualTo(1L);

    // 첫 번째 과목 순서 검증
    assertThat(result.subjects().getFirst().subjectOrder()).isEqualTo(1);
  }

  @Test
  @DisplayName("기본 BE 로드맵을 정상적으로 조회한다")
  void 기본_BE_로드맵_조회() {
    // Given
    when(roadmapRepository.findByRoadmapManagement_RoadmapManagementId(2L))
        .thenReturn(BE_roadmapList);

    doReturn(27L).when(roadmapService).findUserLocationRoadmap(BE_roadmapList);

    // When
    RoadmapFindResDto result = roadmapService.getDefaultRoadmap("BE");

    // Then
    assertThat(result).isNotNull();
    assertThat(result.roadmapName()).isEqualTo("BE_DEFAULT_ROADMAP");
    assertThat(result.subjects()).hasSize(4);
    assertThat(result.userLocationSubjectId()).isEqualTo(27L);
  }

  @Test
  @DisplayName("잘못된 타입의 로드맵 요청 시 예외가 발생한다")
  void 잘못된_로드맵_타입_예외() {
    // When & Then
    assertThatThrownBy(() -> roadmapService.getDefaultRoadmap("MOBILE"))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(StatusCode.ROADMAP_TYPE_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("기존 로드맵이 있는 유저가 FE 기본 로드맵을 저장하면 기존 로드맵은 삭제되고 새 로드맵이 저장된다")
  void FE_기본_로드맵_저장() {
    // Given
    String roadmapType = "FE";
    Long userId = 1L;
    Long roadmapManagementId = 1L;
    Long userLocationSubjectId = 1L;

    Subject subject1 = new Subject(1L, "HTML", "FE", "Y", 1, "", null);
    Subject subject2 = new Subject(2L, "CSS", "FE", "Y", 2, "", null);

    Roadmap default1 = Roadmap.builder().subject(subject1).orderSub(1).build();
    Roadmap default2 = Roadmap.builder().subject(subject2).orderSub(2).build();
    List<Roadmap> defaultRoadmaps = List.of(default1, default2);

    when(roadmapManagementRepository.findByUserId(userId))
        .thenReturn(mock(RoadmapManagement.class));
    when(roadmapRepository.findByRoadmapManagement_RoadmapManagementId(roadmapManagementId))
        .thenReturn(defaultRoadmaps);

    // save 후 getDefaultRoadmap으로 리턴되는 SubjectDto mock
    SubjectDto dto1 = new SubjectDto(1L, "HTML", 1);
    SubjectDto dto2 = new SubjectDto(2L, "CSS", 2);
    List<SubjectDto> subjectDtos = List.of(dto1, dto2);
    doReturn(new RoadmapFindResDto(subjectDtos, "FE_DEFAULT_ROADMAP", userLocationSubjectId))
        .when(roadmapService)
        .getDefaultRoadmap(roadmapType);

    // When
    RoadmapSaveResDto result = roadmapService.saveDefaultRoadmap(roadmapType, userId);

    // Then
    verify(roadmapTransactionalService).deleteRoadmap(userId); // 기존 로드맵 삭제
    verify(roadmapManagementRepository).save(any(RoadmapManagement.class)); // 새 관리 엔티티 저장
    verify(roadmapRepository, times(2)).save(any(Roadmap.class)); // 새 로드맵들 저장

    assertThat(result).isNotNull();
    assertThat(result.uuid()).isEqualTo("user Default Roadmap");
    assertThat(result.userLocationSubjectId()).isEqualTo(userLocationSubjectId);
    assertThat(result.subjects()).hasSize(2);
    assertThat(result.subjects().getFirst().subjectId()).isEqualTo(1L);
  }

  @Test
  @DisplayName("기본 로드맵 사용자에게 저장")
  void 기본_로드맵_사용자에게_저장() {
    // Given
    RoadmapManagement roadmapManagement =
        RoadmapManagement.builder().roadmapManagementId(100L).roadmapNm("사용자 개인 로드맵").build();

    Subject subject1 = new Subject(1L, "HTML", "FE", "Y", 1, "", null);
    Subject subject2 = new Subject(2L, "CSS", "FE", "Y", 2, "", null);

    Roadmap roadmap1 =
        Roadmap.builder()
            .roadmapManagement(roadmapManagement)
            .subject(subject1)
            .orderSub(1)
            .build();

    Roadmap roadmap2 =
        Roadmap.builder()
            .roadmapManagement(roadmapManagement)
            .subject(subject2)
            .orderSub(2)
            .build();

    List<Roadmap> userRoadmaps = List.of(roadmap1, roadmap2);

    when(roadmapRepository.findByRoadmapManagement_RoadmapManagementId(100L))
        .thenReturn(userRoadmaps);

    // 내부 메서드 findUserLocationRoadmap(...) 스텁
    doReturn(1L).when(roadmapService).findUserLocationRoadmap(userRoadmaps);

    // When
    RoadmapFindResDto result = roadmapService.findUserRoadmap(roadmapManagement);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.roadmapName()).isEqualTo("사용자 개인 로드맵");
    assertThat(result.userLocationSubjectId()).isEqualTo(1L);
    assertThat(result.subjects()).hasSize(2);
    assertThat(result.subjects().get(0).subjectId()).isEqualTo(1L);
    assertThat(result.subjects().get(1).subjectId()).isEqualTo(2L);
  }

  @Test
  @DisplayName("사용자 로드맵 조회 성공")
  void 사용자_로드맵_반환() {
    // Given
    RoadmapManagement roadmapManagement =
        RoadmapManagement.builder().roadmapManagementId(10L).roadmapNm("My Roadmap").build();

    Subject subject1 = new Subject(1L, "HTML", "FE", "Y", 1, "", null);
    Subject subject2 = new Subject(2L, "CSS", "FE", "Y", 2, "", null);

    Roadmap roadmap1 =
        Roadmap.builder()
            .roadmapManagement(roadmapManagement)
            .subject(subject1)
            .orderSub(1)
            .isComplete(false)
            .build();

    Roadmap roadmap2 =
        Roadmap.builder()
            .roadmapManagement(roadmapManagement)
            .subject(subject2)
            .orderSub(2)
            .isComplete(true)
            .build();

    List<Roadmap> userRoadmaps = List.of(roadmap1, roadmap2);

    when(roadmapRepository.findByRoadmapManagement_RoadmapManagementId(10L))
        .thenReturn(userRoadmaps);

    doReturn(1L).when(roadmapService).findUserLocationRoadmap(userRoadmaps);

    // When
    RoadmapFindResDto result = roadmapService.findUserRoadmap(roadmapManagement);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.roadmapName()).isEqualTo("My Roadmap");
    assertThat(result.userLocationSubjectId()).isEqualTo(1L);
    assertThat(result.subjects()).hasSize(2);
    assertThat(result.subjects().get(0).subjectId()).isEqualTo(1L);
    assertThat(result.subjects().get(1).subjectId()).isEqualTo(2L);
  }

  @Test
  @DisplayName("로드맵이 비어 있을 경우 예외 발생")
  void 사용자_로드맵_반환_로드맵_없음() {
    // Given
    RoadmapManagement roadmapManagement =
        RoadmapManagement.builder().roadmapManagementId(20L).roadmapNm("Empty Roadmap").build();

    when(roadmapRepository.findByRoadmapManagement_RoadmapManagementId(20L)).thenReturn(List.of());

    // When & Then
    assertThatThrownBy(() -> roadmapService.findUserRoadmap(roadmapManagement))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(StatusCode.ROADMAP_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("완료되지 않은 로드맵 중 가장 낮은 orderSub의 subjectId를 반환한다")
  void 사용자_로드맵_위치_반환_일반작동() {
    // Given
    Subject subject1 = new Subject(1L, "HTML", "FE", "Y", 1, "", null);
    Subject subject2 = new Subject(2L, "CSS", "FE", "Y", 2, "", null);
    Subject subject3 = new Subject(3L, "JS", "FE", "Y", 3, "", null);

    List<Roadmap> roadmaps =
        List.of(
            Roadmap.builder().subject(subject1).orderSub(1).isComplete(true).build(),
            Roadmap.builder().subject(subject2).orderSub(2).isComplete(false).build(),
            Roadmap.builder().subject(subject3).orderSub(3).isComplete(false).build());

    // When
    Long result = roadmapService.findUserLocationRoadmap(roadmaps);

    // Then
    assertThat(result).isEqualTo(2L); // CSS가 첫 번째 미완료
  }

  @Test
  @DisplayName("모든 로드맵이 완료되었으면 null을 반환한다")
  void 사용자_로드맵_위치_반환_모든_과목_이수시() {
    // Given
    Subject subject1 = new Subject(1L, "HTML", "FE", "Y", 1, "", null);
    Subject subject2 = new Subject(2L, "CSS", "FE", "Y", 2, "", null);

    List<Roadmap> roadmaps =
        List.of(
            Roadmap.builder().subject(subject1).orderSub(1).isComplete(true).build(),
            Roadmap.builder().subject(subject2).orderSub(2).isComplete(true).build());

    // When
    Long result = roadmapService.findUserLocationRoadmap(roadmaps);

    // Then
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("회원이면서 로드맵이 있으면 findUserRoadmap을 호출한다")
  void 로드맵_반환_요청시_분기_처리_회원_로드맵_존재() {
    // Given
    Long userId = 1L;
    RoadmapManagement roadmapManagement =
        RoadmapManagement.builder()
            .roadmapManagementId(10L)
            .roadmapNm("My Roadmap")
            .userId(userId)
            .build();

    RoadmapFindResDto expectedDto = new RoadmapFindResDto(List.of(), "My Roadmap", 1L);

    when(roadmapManagementRepository.findByUserId(userId)).thenReturn(roadmapManagement);
    doReturn(expectedDto).when(roadmapService).findUserRoadmap(roadmapManagement);

    // When
    RoadmapFindResDto result = roadmapService.findRoadmap(userId, "ignored");

    // Then
    assertThat(result.roadmapName()).isEqualTo("My Roadmap");
    verify(roadmapService).findUserRoadmap(roadmapManagement);
  }

  @Test
  @DisplayName("회원이지만 로드맵이 없고 uuid가 존재할 경우 saveGuestRoadmap을 호출한다")
  void 로드맵_반환_요청시_분기_처리_회원_로드맵_없고_uuid_있음() {
    // Given
    Long userId = 1L;
    String uuid = "abcd-efgh";

    when(roadmapManagementRepository.findByUserId(userId)).thenReturn(null);

    RoadmapSaveResDto saved =
        new RoadmapSaveResDto(uuid, "테스트용 로드맵", 1L, List.of(new SubjectDto(1L, "HTML", 1)));

    doReturn(saved).when(roadmapService).saveGuestRoadmap(uuid, userId);

    // When
    RoadmapFindResDto result = roadmapService.findRoadmap(userId, uuid);

    // Then
    assertThat(result.roadmapName()).isEqualTo(uuid);
    assertThat(result.subjects()).hasSize(1);
    verify(roadmapService).saveGuestRoadmap(uuid, userId);
  }

  @Test
  @DisplayName("회원이지만 로드맵이 없고 uuid가 'takeit'일 경우 예외 발생")
  void 로드맵_반환_요청시_분기_처리_회원_로드맵_없고_uuid_없음() {
    // Given
    Long userId = 1L;
    String uuid = "takeit";

    when(roadmapManagementRepository.findByUserId(userId)).thenReturn(null);

    // When & Then
    assertThatThrownBy(() -> roadmapService.findRoadmap(userId, uuid))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(StatusCode.DIAGNOSIS_RESPONSE_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("비회원이면서 uuid가 있을 경우 findGuestRoadmap 호출")
  void 로드맵_반환_요청시_분기_처리_비회원_uuid_있음() {
    // Given
    String uuid = "guest-uuid";
    RoadmapFindResDto expected =
        new RoadmapFindResDto(List.of(new SubjectDto(1L, "CSS", 1)), "guest-uuid", 1L);

    doReturn(expected).when(roadmapService).findGuestRoadmap(uuid);

    // When
    RoadmapFindResDto result = roadmapService.findRoadmap(null, uuid);

    // Then
    assertThat(result.roadmapName()).isEqualTo("guest-uuid");
    verify(roadmapService).findGuestRoadmap(uuid);
  }

  @Test
  @DisplayName("비회원이고 uuid가 'takeit'이면 예외 발생")
  void 로드맵_반환_요청시_분기_처리_비회원_uuid_없음() {
    // When & Then
    assertThatThrownBy(() -> roadmapService.findRoadmap(null, "takeit"))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(StatusCode.DIAGNOSIS_RESPONSE_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("게스트 로드맵이 Redis에 존재하면 정상적으로 반환된다")
  void 레디스에_저장된_로드맵_반환() {
    // Given
    String uuid = "guest-123";
    String redisKey = "guest:" + uuid + ":subjects";
    String redisValue = "1,2";

    when(valueOperations.get(redisKey)).thenReturn(redisValue);

    Subject subject1 = new Subject(1L, "HTML", "FE", "Y", 1, "", null);
    Subject subject2 = new Subject(2L, "CSS", "FE", "Y", 2, "", null);

    when(subjectRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(subject1, subject2));

    // When
    RoadmapFindResDto result = roadmapService.findGuestRoadmap(uuid);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.roadmapName()).isEqualTo("guest-123's roadmap");
    assertThat(result.userLocationSubjectId()).isEqualTo(1L);
    assertThat(result.subjects()).hasSize(2);
    assertThat(result.subjects().get(0).subjectName()).isEqualTo("HTML");
  }

  @Test
  @DisplayName("사용자와 과목 ID로 과목 상세 정보를 조회한다")
  void 사용자_로드맵의_과목_데이터_반환() {
    // Given
    Long userId = 1L;
    Long subjectId = 10L;

    Track track = new Track();
    Subject subject = new Subject(subjectId, "HTML", "FE", "Y", 1, "소개입니다", track);
    RoadmapManagement roadmapManagement =
        RoadmapManagement.builder().roadmapManagementId(100L).userId(userId).build();

    Roadmap roadmap =
        Roadmap.builder()
            .roadmapId(999L)
            .roadmapManagement(roadmapManagement)
            .subject(subject)
            .preSubmitCount(3)
            .postSubmitCount(5)
            .build();

    Chapter chapter1 =
        Chapter.builder().chapterName("기본 구조와 시멘틱 태그").chapterOrder(1).subject(subject).build();

    Chapter chapter2 =
        Chapter.builder().chapterName("텍스트 & 목록요소").chapterOrder(2).subject(subject).build();

    List<UserContentResDto> recommendations =
        List.of(
            new UserContentResDto(
                1L, subjectId, "제목1", "https://example.com", "유형1", "", "", "", true, ""),
            new UserContentResDto(
                2L, subjectId, "제목2", "https://example2.com", "유형2", "", "", "", false, ""));

    when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));
    when(chapterRepository.findBySubject(subject)).thenReturn(List.of(chapter1, chapter2));
    when(roadmapManagementRepository.findByUserId(userId)).thenReturn(roadmapManagement);
    when(roadmapRepository.findBySubjectAndRoadmapManagement(subject, roadmapManagement))
        .thenReturn(roadmap);
    when(recommendService.findRecommendations(userId, subjectId)).thenReturn(recommendations);

    // When
    SubjectFindResDto result = subjectService.findUserSubject(userId, subjectId);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.subject_name()).isEqualTo("HTML");
    assertThat(result.subject_overview()).isEqualTo("소개입니다");
    assertThat(result.roadmapId()).isEqualTo(999L);
    assertThat(result.chapters()).hasSize(2);
    assertThat(result.recommendContents()).hasSize(2);
    assertThat(result.preSubmitCount()).isEqualTo(3);
    assertThat(result.postSubmitCount()).isEqualTo(5);
  }

  @Test
  @DisplayName("정상적으로 로드맵 및 관련 데이터를 삭제한다")
  void 로드맵_삭제() {
    // Given
    Long userId = 1L;

    RoadmapManagement roadmapManagement =
        RoadmapManagement.builder().roadmapManagementId(100L).userId(userId).build();

    Subject subject = new Subject();
    Roadmap roadmap1 =
        Roadmap.builder()
            .roadmapId(1L)
            .subject(subject)
            .roadmapManagement(roadmapManagement)
            .build();
    Roadmap roadmap2 =
        Roadmap.builder()
            .roadmapId(2L)
            .subject(subject)
            .roadmapManagement(roadmapManagement)
            .build();

    List<Roadmap> roadmaps = List.of(roadmap1, roadmap2);

    when(roadmapManagementRepository.findByUserId(userId)).thenReturn(roadmapManagement);
    when(roadmapRepository.findByRoadmapManagement_RoadmapManagementId(100L)).thenReturn(roadmaps);

    // When
    injectedRoadmapTransactionalService.deleteRoadmap(userId);

    // Then
    verify(roadmapRepository).deleteAll(roadmaps);
    verify(roadmapManagementRepository).delete(roadmapManagement);
    verify(userContentRepository).deleteByUser_UserId(userId);
    verify(feedbackRepository).deleteByUser_UserId(userId);
    verify(userExamAnswerRepository).deleteByUser_UserId(userId);
  }

  @Test
  @DisplayName("로드맵 삭제 시 로드맵이 존재하지 않으면 예외 발생")
  void 로드맵_삭제_로드맵_없음() {
    // Given
    Long userId = 1L;
    RoadmapManagement roadmapManagement =
        RoadmapManagement.builder().roadmapManagementId(100L).userId(userId).build();

    when(roadmapManagementRepository.findByUserId(userId)).thenReturn(roadmapManagement);
    when(roadmapRepository.findByRoadmapManagement_RoadmapManagementId(100L)).thenReturn(List.of());

    assertThatThrownBy(() -> injectedRoadmapTransactionalService.deleteRoadmap(userId))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(StatusCode.ROADMAP_NOT_FOUND.getMessage());

    verify(roadmapRepository, never()).deleteAll(anyList());
    verify(roadmapManagementRepository, never()).delete(any());
  }

  @Test
  @DisplayName("로드맵 관리 엔티티가 없을 경우 NPE 발생")
  void 로드맵_삭제_관리엔티티_없음() {
    Long userId = 1L;
    when(roadmapManagementRepository.findByUserId(userId)).thenReturn(null);

    assertThatThrownBy(() -> injectedRoadmapTransactionalService.deleteRoadmap(userId))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  @DisplayName("saveRoadmap - 이미 로드맵이 존재할 경우 예외 발생")
  void saveRoadmap_이미_존재() {
    Long userId = 1L;
    when(roadmapManagementRepository.findByUserId(userId))
        .thenReturn(mock(RoadmapManagement.class));

    assertThatThrownBy(
            () -> roadmapService.saveRoadmap(userId, "이미 존재", List.of(1L, 2L), List.of()))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(StatusCode.ALREADY_EXIST_ROADMAP.getMessage());
  }

  @Test
  @DisplayName("saveRoadmap - 존재하지 않는 사용자 예외 발생")
  void saveRoadmap_유저없음() {

    Long userId = 1L;
    when(roadmapManagementRepository.findByUserId(userId)).thenReturn(null);
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> roadmapService.saveRoadmap(userId, "로드맵", List.of(1L, 2L), List.of()))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(StatusCode.USER_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("saveRoadmap - 진단 응답으로 사용자 정보 업데이트")
  void saveRoadmap_공통질문응답() {
    // Given
    Long userId = 1L;
    List<DiagnosisAnswerRequest> answers =
        List.of(
            new DiagnosisAnswerRequest(2L, "1"), // LectureAmount
            new DiagnosisAnswerRequest(3L, "2"), // PriceLevel
            new DiagnosisAnswerRequest(4L, "Y") // likesBooks = true
            );
    Subject subject = new Subject(1L, "HTML", "FE", "Y", 1, "", null);

    when(roadmapManagementRepository.findByUserId(userId)).thenReturn(null);

    User user = mock(User.class);
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(subjectRepository.findAllById(List.of(1L))).thenReturn(List.of(subject));

    // When
    roadmapService.saveRoadmap(userId, "테스트용 로드맵", List.of(1L), answers);

    // Then
    verify(user)
        .updatePreferences(eq(LectureAmount.values()[1]), eq(PriceLevel.values()[2]), eq(true));
    verify(roadmapRepository).saveAll(any());
  }

  @Test
  @DisplayName("saveRoadmap - subjectId에 해당하는 과목이 없으면 예외 발생")
  void saveRoadmap_과목없음() {
    Long userId = 1L;
    DiagnosisAnswerRequest dummyAnswer = new DiagnosisAnswerRequest(2L, "0");
    when(roadmapManagementRepository.findByUserId(userId)).thenReturn(null);

    User user = mock(User.class);
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(subjectRepository.findAllById(List.of(99L))).thenReturn(List.of()); // 과목 존재하지 않으면 빈 리스트 반환

    assertThatThrownBy(
            () -> roadmapService.saveRoadmap(userId, "로드맵", List.of(99L), List.of(dummyAnswer)))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(StatusCode.SUBJECT_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("saveGuestRoadmap - Redis에 subject/answers 없을 때 예외 발생")
  void saveGuestRoadmap_redis데이터_없음() {
    String uuid = "guest-uuid";
    when(redisTemplate.opsForValue().get("guest:" + uuid + ":subjects")).thenReturn(null);
    when(redisTemplate.opsForValue().get("guest:" + uuid + ":answers")).thenReturn(null);

    assertThatThrownBy(() -> roadmapService.saveGuestRoadmap(uuid, 1L))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(StatusCode.ROADMAP_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("saveGuestRoadmap - answers 역직렬화 실패 시 예외 발생")
  void saveGuestRoadmap_역직렬화_실패() throws JsonProcessingException {
    String uuid = "guest-uuid";
    String subjects = "1,2";
    String invalidJson = "invalid-json";

    when(redisTemplate.opsForValue().get("guest:" + uuid + ":subjects")).thenReturn(subjects);
    when(redisTemplate.opsForValue().get("guest:" + uuid + ":answers")).thenReturn(invalidJson);

    when(objectMapper.readValue(eq(invalidJson), any(TypeReference.class)))
        .thenThrow(JsonProcessingException.class);

    assertThatThrownBy(() -> roadmapService.saveGuestRoadmap(uuid, 1L))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("answers 역직렬화 실패");
  }

  @Test
  @DisplayName("saveGuestRoadmap - 정상적으로 게스트 로드맵 저장 성공")
  void saveGuestRoadmap_정상() throws JsonProcessingException {
    // Given
    String uuid = "guest-uuid";
    Long userId = 100L;

    String redisSubjects = "1,2";
    String redisAnswersJson =
        """
        [
            {"questionId":2,"answer":"1"},
            {"questionId":3,"answer":"2"},
            {"questionId":4,"answer":"Y"}
        ]
        """;

    List<Long> subjectIds = List.of(1L, 2L);
    List<DiagnosisAnswerRequest> parsedAnswers =
        List.of(
            new DiagnosisAnswerRequest(2L, "1"),
            new DiagnosisAnswerRequest(3L, "2"),
            new DiagnosisAnswerRequest(4L, "Y"));

    Subject subject1 = new Subject(1L, "HTML", "FE", "Y", 1, "", null);
    Subject subject2 = new Subject(1L, "HTML", "FE", "Y", 1, "", null);

    when(redisTemplate.opsForValue().get("guest:" + uuid + ":subjects")).thenReturn(redisSubjects);
    when(redisTemplate.opsForValue().get("guest:" + uuid + ":answers"))
        .thenReturn(redisAnswersJson);
    when(objectMapper.readValue(anyString(), any(TypeReference.class))).thenReturn(parsedAnswers);
    when(subjectRepository.findAllById(subjectIds)).thenReturn(List.of(subject1, subject2));

    doNothing().when(roadmapService).saveRoadmap(userId, "게스트 로드맵", subjectIds, parsedAnswers);

    // When
    RoadmapSaveResDto result = roadmapService.saveGuestRoadmap(uuid, userId);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.subjects()).hasSize(2);
    assertThat(result.uuid()).isEqualTo("uuid로 로드맵 생성 완료");

    verify(redisTemplate).delete("guest:" + uuid + ":subjects");
    verify(redisTemplate).delete("guest:" + uuid + ":answers");
  }

  @Test
  @DisplayName("updateRoadmap - 기존 과목 순서만 업데이트")
  void updateRoadmap_기존과목순서_업데이트() {
    Long userId = 1L;
    RoadmapManagement roadmapManagement =
        RoadmapManagement.builder().roadmapManagementId(200L).userId(userId).build();

    Subject subject1 = new Subject(1L, "HTML", "FE", "Y", 1, "", null);

    Roadmap existing =
        Roadmap.builder()
            .roadmapManagement(roadmapManagement)
            .subject(subject1)
            .orderSub(1)
            .build();

    when(roadmapManagementRepository.findByUserId(userId)).thenReturn(roadmapManagement);
    when(roadmapRepository.findByRoadmapManagement_RoadmapManagementId(200L))
        .thenReturn(List.of(existing));

    List<SubjectDto> updateSubjects = List.of(new SubjectDto(1L, "HTML", 5));

    roadmapService.updateRoadmap(userId, updateSubjects);

    assertThat(existing.getOrderSub()).isEqualTo(5); // 순서 변경 확인
    verify(roadmapRepository).saveAll(any());
  }
}

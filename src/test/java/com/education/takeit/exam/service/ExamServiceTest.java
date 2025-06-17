package com.education.takeit.exam.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

import com.education.takeit.exam.dto.*;
import com.education.takeit.exam.entity.Exam;
import com.education.takeit.exam.repository.ExamRepository;
import com.education.takeit.global.client.AIClient;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.kafka.feedback.dto.FeedbackRequestDto;
import com.education.takeit.kafka.feedback.producer.FeedbackKafkaProducer;
import com.education.takeit.kafka.recommend.dto.RecomRequestDto;
import com.education.takeit.kafka.recommend.producer.RecomKafkaProducer;
import com.education.takeit.roadmap.entity.Roadmap;
import com.education.takeit.roadmap.entity.RoadmapManagement;
import com.education.takeit.roadmap.entity.Subject;
import com.education.takeit.roadmap.entity.Track;
import com.education.takeit.roadmap.repository.RoadmapRepository;
import com.education.takeit.roadmap.repository.SubjectRepository;
import com.education.takeit.solution.repository.UserExamAnswerRepository;
import com.education.takeit.user.entity.LoginType;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class ExamServiceTest {
  @InjectMocks private ExamService examService;
  @Mock private AIClient aiClient;
  @Mock private RoadmapRepository roadmapRepository;
  @Mock private FeedbackKafkaProducer feedbackKafkaProducer;
  @Mock private RecomKafkaProducer recomKafkaProducer;
  @Mock private ExamLevelCalculator examLevelCalculator;
  @Mock private UserRepository userRepository;
  @Mock private SubjectRepository subjectRepository;
  @Mock private ExamRepository examRepository;
  @Mock private UserExamAnswerRepository userExamAnswerRepository;

  @Test
  @DisplayName("사전 평가 문제 조회")
  void testFindPreExam() {
    Long userId = 1L;
    Long subjectId = 10L;
    ExamResDto exam1 =
        ExamResDto.builder()
            .questionId(1L)
            .question("문제1")
            .choice1("A")
            .choice2("B")
            .choice3("C")
            .choice4("D")
            .answerNum(1)
            .chapterNum(1)
            .chapterName("챕터1")
            .difficulty("low")
            .build();

    ExamResDto exam2 =
        ExamResDto.builder()
            .questionId(2L)
            .question("문제2")
            .choice1("A")
            .choice2("B")
            .choice3("C")
            .choice4("D")
            .answerNum(2)
            .chapterNum(2)
            .chapterName("챕터2")
            .difficulty("low")
            .build();

    List<ExamResDto> mockResult = List.of(exam1, exam2);
    when(aiClient.getPreExam(userId, subjectId)).thenReturn(mockResult);

    List<ExamResDto> result = examService.findPreExam(userId, subjectId);

    assertThat(result).hasSize(2);
    verify(aiClient).getPreExam(userId, subjectId);
  }

  @Test
  @DisplayName("사전 평가 문제가 비어있을 경우 예외 발생")
  void testFindePreExam_emptyResult() {
    Long userId = 1L;
    Long subjectId = 10L;

    when(aiClient.getPreExam(userId, subjectId)).thenReturn(Collections.emptyList());

    assertThatThrownBy(() -> examService.findPreExam(userId, subjectId))
        .isInstanceOf(CustomException.class)
        .hasMessage(StatusCode.EMPTY_RESULT.getMessage());

    verify(aiClient).getPreExam(userId, subjectId);
  }

  @Test
  @DisplayName("사후 평가 문제 조회")
  void TestFindPostExam() {
    Long userId = 1L;
    Long subjectId = 2L;

    ExamResDto exam1 =
        ExamResDto.builder()
            .questionId(1L)
            .question("문제1")
            .choice1("A")
            .choice2("B")
            .choice3("C")
            .choice4("D")
            .answerNum(2)
            .chapterNum(1)
            .chapterName("챕터1")
            .difficulty("low")
            .build();

    ExamResDto exam2 =
        ExamResDto.builder()
            .questionId(1L)
            .question("문제1")
            .choice1("A")
            .choice2("B")
            .choice3("C")
            .choice4("D")
            .answerNum(2)
            .chapterNum(1)
            .chapterName("챕터1")
            .difficulty("low")
            .build();

    List<ExamResDto> mockResult = List.of(exam1, exam2);

    when(aiClient.getPostExam(userId, subjectId)).thenReturn(mockResult);

    List<ExamResDto> result = examService.findPostExam(userId, subjectId);

    assertThat(result).hasSize(2);
    verify(aiClient).getPostExam(userId, subjectId);
  }

  @Test
  @DisplayName("사후 평가 문제가 비어있을 경우 예외 발생")
  void TestFindPostExam_emptyResult() {
    Long userId = 1L;
    Long subjectId = 2L;

    when(aiClient.getPostExam(userId, subjectId)).thenReturn(Collections.emptyList());

    assertThatThrownBy(() -> examService.findPostExam(userId, subjectId))
        .isInstanceOf(CustomException.class)
        .hasMessage(StatusCode.EMPTY_RESULT.getMessage());

    verify(aiClient).getPostExam(userId, subjectId);
  }

  @Test
  @DisplayName("사전 평가 결과 저장")
  void testSubmitPreExam() {
    // Given
    Long userId = 1L;
    Long roadmapId = 10L;
    Long subjectId = 100L;
    String startDate = "2024-06-01";
    Long duration = 300L;
    int submitCnt = 1;

    User user =
        User.builder()
            .email("test@email.com")
            .nickname("테스트유저")
            .password("1234")
            .loginType(LoginType.LOCAL)
            .build();

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    Subject subject =
        Subject.builder()
            .subId(100L)
            .subNm("Spring")
            .subType("BE")
            .subEssential("Y")
            .baseSubOrder(1)
            .subOverview("스프링 기본 개념")
            .track(new Track())
            .build();

    when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));

    when(examRepository.findByExamContentAndSubject_SubId("문제1", subject.getSubId()))
        .thenReturn(
            Optional.of(
                Exam.builder()
                    .examId(1L)
                    .examContent("문제1")
                    .examAnswer(2)
                    .examLevel("중")
                    .option1("A")
                    .option2("B")
                    .option3("C")
                    .option4("D")
                    .solution("정답 해설입니다.")
                    .subject(subject)
                    .build()));

    ExamAnswerDto answer = new ExamAnswerDto(1L, "문제1", 1, "챕터1", "중", true, 2);
    List<ExamAnswerDto> answers = List.of(answer);

    ExamAnswerResDto examAnswerRes =
        new ExamAnswerResDto(roadmapId, subjectId, startDate, duration, submitCnt, answers);

    Roadmap roadmap =
        Roadmap.builder().roadmapId(roadmapId).subject(subject).preSubmitCount(0).build();

    when(roadmapRepository.findByRoadmapId(roadmapId)).thenReturn(roadmap);

    ExamService spyService = Mockito.spy(examService);
    SubjectResultDto subjectResult =
        new SubjectResultDto(subjectId, startDate, duration, submitCnt + 1, 2, 1, 1);
    ChapterResultDto chapterResult = new ChapterResultDto(1, "챕터1", false, 1, 1);
    doReturn(subjectResult).when(spyService).calculateSubjectResultForPre(examAnswerRes);
    doReturn(List.of(chapterResult)).when(spyService).calculateChapterResults(answers);

    // When
    ResponseEntity<Void> response = spyService.submitPreExam(userId, examAnswerRes);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    verify(aiClient).postPreExam(eq(userId), any(ExamResultDto.class));
    verify(roadmapRepository).save(any(Roadmap.class));
    verify(feedbackKafkaProducer).publish(any(FeedbackRequestDto.class));
    verify(recomKafkaProducer).publish(any(RecomRequestDto.class));
  }

  @Test
  @DisplayName("사후 평가 결과 저장")
  void testSubmitPostExam() {
    Long userId = 1L;
    Long roadmapId = 10L;
    Long subjectId = 100L;
    String startDate = "2024-06-01";
    Long duration = 300L;
    int submitCnt = 1;

    User user =
        User.builder()
            .email("test@email.com")
            .nickname("테스트유저")
            .password("1234")
            .loginType(LoginType.LOCAL)
            .build();

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    Subject subject =
        Subject.builder()
            .subId(100L)
            .subNm("Spring")
            .subType("BE")
            .subEssential("Y")
            .baseSubOrder(1)
            .subOverview("스프링 기본 개념")
            .track(new Track())
            .build();

    when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));

    when(examRepository.findByExamContentAndSubject_SubId("문제1", subject.getSubId()))
        .thenReturn(
            Optional.of(
                Exam.builder()
                    .examId(1L)
                    .examContent("문제1")
                    .examAnswer(2)
                    .examLevel("중")
                    .option1("A")
                    .option2("B")
                    .option3("C")
                    .option4("D")
                    .solution("정답 해설입니다.")
                    .subject(subject)
                    .build()));

    ExamAnswerDto answer = new ExamAnswerDto(1L, "문제1", 1, "챕터1", "중", true, 2);
    List<ExamAnswerDto> answers = List.of(answer);

    ExamAnswerResDto examAnswerRes =
        new ExamAnswerResDto(roadmapId, subjectId, startDate, duration, submitCnt, answers);

    Roadmap roadmap =
        Roadmap.builder()
            .roadmapId(roadmapId)
            .orderSub(1)
            .subject(subject)
            .roadmapManagement(new RoadmapManagement())
            .isComplete(false)
            .preSubmitCount(0)
            .postSubmitCount(0)
            .level(0)
            .build();

    when(roadmapRepository.findByRoadmapId(roadmapId)).thenReturn(roadmap);

    ExamService spyService = Mockito.spy(examService);
    SubjectResultDto subjectResult =
        new SubjectResultDto(subjectId, startDate, duration, submitCnt + 1, 3, 1, 1);
    ChapterResultDto chapterResult = new ChapterResultDto(1, "챕터1", false, 1, 1);
    doReturn(subjectResult).when(spyService).calculateSubjectResultForPost(roadmap, examAnswerRes);
    doReturn(List.of(chapterResult)).when(spyService).calculateChapterResults(answers);

    // When
    ResponseEntity<Void> response = spyService.submitPostExam(userId, examAnswerRes);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    verify(aiClient).postPostExam(eq(userId), any(ExamResultDto.class));
    verify(roadmapRepository).save(any(Roadmap.class));
    verify(feedbackKafkaProducer).publish(any(FeedbackRequestDto.class));
  }
}

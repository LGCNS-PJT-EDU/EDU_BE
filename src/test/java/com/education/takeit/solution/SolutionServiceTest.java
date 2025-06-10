package com.education.takeit.solution;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.education.takeit.exam.entity.Exam;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.roadmap.entity.Subject;
import com.education.takeit.solution.dto.SolutionResDto;
import com.education.takeit.solution.entity.UserExamAnswer;
import com.education.takeit.solution.repository.UserExamAnswerRepository;
import com.education.takeit.solution.service.SolutionService;
import java.lang.reflect.Field;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SolutionServiceTest {

  @InjectMocks private SolutionService solutionService;

  @Mock private UserExamAnswerRepository userExamAnswerRepository;

  private Long userId;
  private Long subjectId;
  private Subject subject;
  private Exam exam;
  private UserExamAnswer userExamAnswer;

  private Exam createExam(Subject subject) {
    Exam exam = new Exam();
    setField(exam, "examId", 100L);
    setField(exam, "examContent", "자바의 기본 자료형은?");
    setField(exam, "examAnswer", 2);
    setField(exam, "examLevel", "하");
    setField(exam, "option1", "String");
    setField(exam, "option2", "int");
    setField(exam, "option3", "List");
    setField(exam, "option4", "Map");
    setField(exam, "solution", "int는 기본 자료형입니다.");
    setField(exam, "subject", subject);
    return exam;
  }

  private void setField(Object target, String fieldName, Object value) {
    try {
      Field field = target.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(target, value);
    } catch (Exception e) {
      throw new RuntimeException("리플렉션을 통한 필드 설정 실패: " + fieldName, e);
    }
  }

  @BeforeEach
  void setUp() {
    userId = 1L;
    subjectId = 10L;

    subject = Subject.builder().subId(subjectId).subNm("Java").build();

    exam = createExam(subject);

    userExamAnswer =
        UserExamAnswer.builder()
            .userAnswer(1)
            .isPre(true)
            .nth(1)
            .exam(exam)
            .subject(subject)
            .build();
  }

  @Test
  @DisplayName("사용자 해설 정상 조회")
  void 사용자_해설_정상_조회() {
    // given
    when(userExamAnswerRepository.findByUser_UserIdAndSubject_SubId(userId, subjectId))
        .thenReturn(List.of(userExamAnswer));

    // when
    List<SolutionResDto> result = solutionService.findAllUserSolutions(userId, subjectId);

    // then
    SolutionResDto dto = result.getFirst();
    assertThat(dto.subNm()).isEqualTo("Java");
    assertThat(dto.examContent()).isEqualTo("자바의 기본 자료형은?");
    assertThat(dto.option1()).isEqualTo("String");
    assertThat(dto.option2()).isEqualTo("int");
    assertThat(dto.option3()).isEqualTo("List");
    assertThat(dto.option4()).isEqualTo("Map");
    assertThat(dto.examAnswer()).isEqualTo(2);
    assertThat(dto.userAnswer()).isEqualTo(1);
    assertThat(dto.solution()).isEqualTo("int는 기본 자료형입니다.");
    assertThat(dto.examLevel()).isEqualTo("하");
    assertThat(dto.isPre()).isTrue();
    assertThat(dto.nth()).isEqualTo(1);

    verify(userExamAnswerRepository).findByUser_UserIdAndSubject_SubId(userId, subjectId);
  }

  @Test
  @DisplayName("사용자 해설 조회 실패 - 풀이가 없는 경우 예외 발생")
  void 사용자_해설_조회_실패_풀이없음() {
    // given
    when(userExamAnswerRepository.findByUser_UserIdAndSubject_SubId(userId, subjectId))
        .thenReturn(List.of());

    // when & then
    assertThatThrownBy(() -> solutionService.findAllUserSolutions(userId, subjectId))
        .isInstanceOf(CustomException.class)
        .extracting("statusCode")
        .isEqualTo(StatusCode.NOT_FOUND_SOLUTION);

    verify(userExamAnswerRepository).findByUser_UserIdAndSubject_SubId(userId, subjectId);
  }
}

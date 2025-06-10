package com.education.takeit.interview.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

import com.education.takeit.interview.dto.InterviewContentResDto;
import com.education.takeit.interview.dto.InterviewHistoryResDto;
import com.education.takeit.interview.entity.Interview;
import com.education.takeit.interview.entity.UserInterviewReply;
import com.education.takeit.interview.repository.InterviewRepository;
import com.education.takeit.interview.repository.UserInterviewReplyRepository;
import com.education.takeit.roadmap.entity.Subject;
import com.education.takeit.roadmap.entity.Track;
import com.education.takeit.user.entity.LoginType;
import com.education.takeit.user.entity.User;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InterviewServiceTest {

  @InjectMocks private InterviewService interviewService;
  @Mock private InterviewRepository interviewRepository;
  @Mock private UserInterviewReplyRepository replyRepository;

  @Test
  @DisplayName("특정 과목에 대한 면접 질문을 랜덤 5개 조회")
  void testGetInterviews() {
    List<Long> subjectIds = List.of(1L, 2L);
    Long userId = 100L;

    Subject subject =
        Subject.builder()
            .subId(1L)
            .subNm("Spring")
            .subType("BE")
            .subEssential("Y")
            .baseSubOrder(1)
            .subOverview("Spring 핵심 개념")
            .track(new Track())
            .build();

    // 면접 질문 10개 생성
    List<Interview> mockInterviews =
        IntStream.rangeClosed(1, 10)
            .mapToObj(
                i ->
                    Interview.builder()
                        .interviewId((long) i)
                        .interviewContent("Q" + i)
                        .subject(subject) // 테스트용 공통 subject
                        .build())
            .collect(Collectors.toList());

    // 최대 회차 3으로 설정
    when(replyRepository.findMaxNthByUserId(userId)).thenReturn(Optional.of(3));
    when(interviewRepository.findBySubject_SubIdIn(subjectIds)).thenReturn(mockInterviews);

    List<InterviewContentResDto> result = interviewService.getInterview(subjectIds, userId);

    // 질문 수 5개 맞는지
    assertThat(result).hasSize(5);

    for (InterviewContentResDto dto : result) {
      assertThat(dto.subjectId()).isEqualTo(subject.getSubId());
      assertThat(dto.subjectName()).isEqualTo(subject.getSubNm());
      assertThat(dto.nth()).isEqualTo(4); // 현재 회차가 maxNth + 1인지
    }
  }

  @Test
  @DisplayName("과목 ID 리스트가 비어있을 경우 빈 질문 리스트 반환")
  void testGetInterviewWithEmptySubjectIds() {
    List<InterviewContentResDto> result =
        interviewService.getInterview(Collections.emptyList(), 100L);
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("사용자의 역대 면접 기록을 회차별로 조회 및 반환")
  void testGetInterviewHistory() {
    Long userId = 1L;

    User user =
        User.builder()
            .email("test@example.com")
            .nickname("nickname")
            .password("encodedPassword")
            .loginType(LoginType.LOCAL)
            .build();

    Subject subject =
        Subject.builder()
            .subId(10L)
            .subNm("Spring")
            .subType("BE")
            .subEssential("Y")
            .baseSubOrder(1)
            .subOverview("Spring 핵심 개념")
            .track(new Track())
            .build();

    Interview interview =
        Interview.builder()
            .interviewContent("면접 질문 예시")
            .interviewAnswer("모범 답변")
            .subject(subject)
            .build();

    UserInterviewReply reply =
        UserInterviewReply.builder()
            .replyId(1L)
            .userReply("사용자 답변")
            .interview(interview)
            .user(user)
            .aiFeedback("AI 피드백")
            .nth(1)
            .build();

    List<UserInterviewReply> replyList = List.of(reply);

    when(replyRepository.findByUser_UserId(userId)).thenReturn(replyList);

    List<InterviewHistoryResDto> result = interviewService.getInterviewHistory(userId);

    assertThat(result).hasSize(1);
    InterviewHistoryResDto dto = result.get(0);

    assertThat(dto.interviewContent()).isEqualTo("면접 질문 예시");
    assertThat(dto.subId()).isEqualTo(10L);
    assertThat(dto.nth()).isEqualTo(1);
    assertThat(dto.userReply()).isEqualTo("사용자 답변");
    assertThat(dto.aiFeedback()).isEqualTo("AI 피드백");
    assertThat(dto.interviewAnswer()).isEqualTo("모범 답변");

    verify(replyRepository, times(1)).findByUser_UserId(userId);
  }
}

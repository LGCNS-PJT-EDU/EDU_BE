package com.education.takeit.interview.service;

import com.education.takeit.global.client.AIClient;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.interview.dto.*;
import com.education.takeit.interview.entity.Interview;
import com.education.takeit.interview.entity.UserInterviewReply;
import com.education.takeit.interview.repository.InterviewRepository;
import com.education.takeit.interview.repository.UserInterviewReplyRepository;
import com.education.takeit.roadmap.entity.Subject;
import com.education.takeit.roadmap.entity.Track;
import com.education.takeit.roadmap.repository.RoadmapRepository;
import com.education.takeit.roadmap.repository.SubjectRepository;
import com.education.takeit.user.entity.LoginType;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InterviewServiceTest {

    @InjectMocks private InterviewService interviewService;
    @Mock
    private InterviewRepository interviewRepository;
    @Mock
    private UserInterviewReplyRepository replyRepository;
    @Mock
    private SubjectRepository subjectRepository;
    @Mock
    private RoadmapRepository roadmapRepository;
    @Mock
    private AIClient aiClient;
    @Mock
    private UserRepository userRepository;


    @Test
    @DisplayName("특정 과목에 대한 면접 질문을 랜덤 5개 조회")
    void testGetInterviews(){
        List<Long> subjectIds = List.of(1L,2L);
        Long userId = 100L;

        Subject subject = Subject.builder()
                .subId(1L)
                .subNm("Spring")
                .subType("BE")
                .subEssential("Y")
                .baseSubOrder(1)
                .subOverview("Spring 핵심 개념")
                .track(new Track())
                .build();

        // 면접 질문 10개 생성
        List<Interview> mockInterviews = IntStream.rangeClosed(1,10)
                .mapToObj(i-> Interview.builder()
                        .interviewId((long)i)
                        .interviewContent("Q"+i)
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
            assertThat(dto.nth()).isEqualTo(4);     // 현재 회차가 maxNth + 1인지
        }
    }

    @Test
    @DisplayName("과목 ID 리스트가 비어있을 경우 예외 발생")
    void testFetInterviewWithEmptySubjectIds(){
        Long userId=100L;
        List<Long> emptySubjectIds = Collections.emptyList();

        assertThatThrownBy(() -> interviewService.getInterview(emptySubjectIds, userId))
                .isInstanceOf(CustomException.class)
                .hasMessage(StatusCode.SUBJECT_ID_REQUIRED.getMessage());
    }


    @Test
    @DisplayName("사용자의 역대 면접 기록을 회차별로 조회 및 반환")
    void testGetInterviewHistory(){
        Long userId = 1L;

        User user = User.builder()
                .email("test@example.com")
                .nickname("nickname")
                .password("encodedPassword")
                .loginType(LoginType.LOCAL)
                .build();

        Subject subject = Subject.builder()
                .subId(10L)
                .subNm("Spring")
                .subType("BE")
                .subEssential("Y")
                .baseSubOrder(1)
                .subOverview("Spring 핵심 개념")
                .track(new Track())
                .build();

        Interview interview = Interview.builder()
                .interviewContent("면접 질문 예시")
                .interviewAnswer("모범 답변")
                .subject(subject)
                .build();

        UserInterviewReply reply = UserInterviewReply.builder()
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

        verify(replyRepository,times(1)).findByUser_UserId(userId);

    }
    @Test
    @DisplayName("모든 과목을 반환, 사용자의 로드맵에 있는 과목과 없는 과목을 구분하여 반환")
    void testGetAllInterviewSubjects(){
        Long userId=1L;
// (Long subId, String subjectNm, Boolean isComplete)
        List<SubjectInfo> allSubjects = List.of(
                new SubjectInfo(3L, "과목 3",true),
                new SubjectInfo(41L, "과목 41",true),
                new SubjectInfo(6L, "과목 6",true),
                new SubjectInfo(37L, "과목 37",true),
                new SubjectInfo(7L, "과목 7",true),
                new SubjectInfo(38L, "과목 38",true),
                new SubjectInfo(50L, "과목 50",true)
        );

        List<SubjectInfo> existingSubjects = List.of(
                new SubjectInfo(3L, "과목 3",true),
                new SubjectInfo(38L, "과목 38",true)
        );

        when(subjectRepository.findAllSubjectInfos()).thenReturn(allSubjects);
        when(roadmapRepository.findSubjectInfosByUserId(userId)).thenReturn(existingSubjects);

        InterviewAllSubIdResDto result = interviewService.getInterviewAllSubId(userId);

        List<SubjectInfo> expectedExisting = existingSubjects;
        List<Long> expectedMissingIds = List.of(6L,37L,50L); // 41,7 제거됨

        List<Long> actualMissingIds = result.missingSubjectIds().stream()
                .map(SubjectInfo::subId)
                .collect(Collectors.toList());

        assertThat(result.existingSubjectIds()).containsExactlyElementsOf(expectedExisting);
        assertThat(actualMissingIds).containsExactlyInAnyOrderElementsOf(expectedMissingIds);

    }
    @Test
    @DisplayName("면접 답변 저장 및 AI 피드백 요청")
    void testSaveReplyAndRequestFeedback() {

        Long userId = 1L;

        User user = User.builder()
                .email("test@email.com")
                .nickname("테스트유저")
                .password("1234")
                .loginType(LoginType.LOCAL)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));


        Subject subject = Subject.builder()
                .subId(1L)
                .subNm("Spring")
                .subType("BE")
                .subEssential("Y")
                .baseSubOrder(1)
                .subOverview("스프링 핵심 개념")
                .track(new Track())
                .build();

        List<AiFeedbackReqDto> requestList = new ArrayList<>();
        List<InterviewFeedbackResDto> expectedFeedbacks = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            Long interviewId = 100L + i;
            String reply = "답변" + i;

            AiFeedbackReqDto reqDto = new AiFeedbackReqDto(interviewId, "면접 질문",reply);
            requestList.add(reqDto);

            Interview interview = Interview.builder()
                    .interviewId(interviewId)
                    .interviewContent("질문" + i)
                    .interviewAnswer("답변" + i)
                    .subject(subject)
                    .build();

            when(interviewRepository.findById(interviewId)).thenReturn(Optional.of(interview));

            // AI 피드백 응답 DTO
            InterviewFeedbackResDto resDto = new InterviewFeedbackResDto(
                    "피드백" + i,
                    "요약" + i,
                    "모범답안" + i,
                    List.of("키워드" + i)
            );

            when(aiClient.getInterviewFeedback(userId, reqDto)).thenReturn(resDto);

            expectedFeedbacks.add(resDto);
        }

        when(replyRepository.save(any(UserInterviewReply.class))).thenReturn(null);

        InterviewAllReplyReqDto requestDto = new InterviewAllReplyReqDto(requestList,1);

        // When
        List<InterviewFeedbackResDto> result =
                interviewService.saveReplyAndRequestFeedback(userId, requestDto);

        // Then
        assertThat(result).hasSize(5);
        assertThat(result).containsExactlyElementsOf(expectedFeedbacks);

        verify(userRepository).findById(userId);
        verify(aiClient, times(5)).getInterviewFeedback(eq(userId), any(AiFeedbackReqDto.class));
        verify(interviewRepository, times(5)).findById(anyLong());
        verify(replyRepository, times(5)).save(any(UserInterviewReply.class));
    }



}

package com.education.takeit.interview.service;

import com.education.takeit.global.client.OpenAiRestClient;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.interview.dto.InterviewContentResDto;
import com.education.takeit.interview.dto.InterviewFeedbackResDto;
import com.education.takeit.interview.dto.UserInterviewReplyReqDto;
import com.education.takeit.interview.entity.Interview;
import com.education.takeit.interview.entity.UserInterviewReply;
import com.education.takeit.interview.repository.InterviewRepository;
import com.education.takeit.interview.repository.UserInterviewReplyRepository;
import com.education.takeit.roadmap.entity.Subject;
import com.education.takeit.roadmap.repository.SubjectRepository;
import com.education.takeit.user.entity.User;
import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InterviewService {
  private final InterviewRepository interviewRepository;
  private final SubjectRepository subjectRepository;
  private final UserInterviewReplyRepository replyRepository;
  private final OpenAiRestClient openAiRestClient;

  public List<InterviewContentResDto> getInterview(Long subjectId) {
    // subjectId 로 subject 정보 조회
    Subject subject =
        subjectRepository
            .findById(subjectId)
            .orElseThrow(() -> new CustomException(StatusCode.SUBJECT_NOT_FOUND));

    List<Interview> interviewList = interviewRepository.findBySubject(subject);
    Collections.shuffle(interviewList);
    return interviewList.stream()
        .limit(3)
        .map(i -> new InterviewContentResDto(i.getInterviewId(), i.getInterviewContent()))
        .toList();
  }

  @Transactional
  public InterviewFeedbackResDto saveReplyAndRequestFeedback(
      UserInterviewReplyReqDto reqDto, User user) {
    Interview interview =
        interviewRepository
            .findById(reqDto.interviewId())
            .orElseThrow(() -> new CustomException(StatusCode.INTERVIEW_NOT_FOUND));

    String bestAnswer = interview.getInterviewAnswer();
    String prompt =
        String.format(
            """
            면접 질문에 대한 사용자 응답과 모범 답안이 있습니다.

            [사용자 응답]
            %s

            [모범 답안]
            %s

            사용자 응답과 모범 답안을 비교해서 사용자 응답에 대해 개선이 필요한 부분을 구체적으로 설명해줘.
            """,
            reqDto.userReply(), bestAnswer);
    String feedback = openAiRestClient.requestInterviewFeedback(prompt);
    UserInterviewReply reply =
            UserInterviewReply.builder()
                    .userReply(reqDto.userReply())
                    .interview(interview)
                    .user(user)
                    .aiFeedback(feedback)
                    .build();
    replyRepository.save(reply);

    return new InterviewFeedbackResDto(feedback);
  }
}

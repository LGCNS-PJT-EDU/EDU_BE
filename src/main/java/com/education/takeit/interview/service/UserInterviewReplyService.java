package com.education.takeit.interview.service;

import com.education.takeit.global.client.OpenAiRestClient;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.interview.dto.InterviewFeedbackResDto;
import com.education.takeit.interview.dto.UserInterviewReplyReqDto;
import com.education.takeit.interview.entity.Interview;
import com.education.takeit.interview.entity.UserInterviewReply;
import com.education.takeit.interview.repository.InterviewRepository;
import com.education.takeit.interview.repository.UserInterviewReplyRepository;
import com.education.takeit.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserInterviewReplyService {

  private final InterviewRepository interviewRepository;
  private final UserInterviewReplyRepository replyRepository;
  private final OpenAiRestClient openAiRestClient;

  @Transactional
  public InterviewFeedbackResDto saveReplyAndRequestFeedback(
      UserInterviewReplyReqDto reqDto, User user) {
    Interview interview =
        interviewRepository
            .findById(reqDto.interviewId())
            .orElseThrow(() -> new CustomException(StatusCode.INTERVIEW_NOT_FOUND));

    UserInterviewReply reply =
        UserInterviewReply.builder()
            .userReply(reqDto.userReply())
            .interview(interview)
            .user(user)
            .build();

    replyRepository.save(reply);

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
    return new InterviewFeedbackResDto(feedback);
  }
}

package com.education.takeit.interview.service;

import com.education.takeit.global.client.AIClient;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.interview.dto.*;
import com.education.takeit.interview.entity.Interview;
import com.education.takeit.interview.entity.UserInterviewReply;
import com.education.takeit.interview.repository.InterviewRepository;
import com.education.takeit.interview.repository.UserInterviewReplyRepository;
import com.education.takeit.roadmap.repository.RoadmapRepository;
import com.education.takeit.roadmap.repository.SubjectRepository;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewService {
  private final InterviewRepository interviewRepository;
  private final SubjectRepository subjectRepository;
  private final UserInterviewReplyRepository replyRepository;
  private final AIClient aiClient;
  private final UserRepository userRepository;
  private final RoadmapRepository roadmapRepository;

  public List<InterviewContentResDto> getInterview(List<Long> subjectIds, Long userId) {
    if (subjectIds == null || subjectIds.isEmpty()) {
      throw new CustomException(StatusCode.SUBJECT_ID_REQUIRED);
    }
    int currentNth = replyRepository.findMaxNthByUserId(userId).orElse(0) + 1;
    List<Interview> interviewList = interviewRepository.findBySubject_SubIdIn(subjectIds);

    Collections.shuffle(interviewList);
    return interviewList.stream()
        .limit(5)
        .map(
            i ->
                new InterviewContentResDto(
                    i.getInterviewId(),
                    i.getInterviewContent(),
                    i.getSubject().getSubId(),
                    i.getSubject().getSubNm(),
                    currentNth))
        .toList();
  }

  public List<InterviewHistoryResDto> getInterviewHistory(Long userId) {
    List<UserInterviewReply> replyList = replyRepository.findByUser_UserId(userId);
    return replyList.stream()
        .map(
            r ->
                InterviewHistoryResDto.builder()
                    .interviewId(r.getInterview().getInterviewId())
                    .interviewContent(r.getInterview().getInterviewContent())
                    .subId(r.getInterview().getSubject().getSubId())
                    .nth(r.getNth())
                    .userReply(r.getUserReply())
                    .aiFeedback(r.getAiFeedback())
                    .interviewAnswer(r.getInterview().getInterviewAnswer())
                    .build())
        .toList();
  }

  public List<InterviewFeedbackResDto> saveReplyAndRequestFeedback(
      Long userId, InterviewAllReplyReqDto interviewAllReplyReqDto) {

    try {
      User user =
          userRepository
              .findById(userId)
              .orElseThrow(() -> new CustomException(StatusCode.USER_NOT_FOUND));

      List<AiFeedbackReqDto> answers = interviewAllReplyReqDto.answers();
      List<InterviewFeedbackResDto> feedbacks = aiClient.getInterviewFeedback(userId, answers);
      System.out.println(feedbacks);

      List<UserInterviewReply> replies = new ArrayList<>();

      for (int i = 0; i < answers.size(); i++) {
        AiFeedbackReqDto dto = answers.get(i);
        InterviewFeedbackResDto feedback = feedbacks.get(i);

        Interview interview =
            interviewRepository
                .findById(dto.interviewId())
                .orElseThrow(() -> new CustomException(StatusCode.INTERVIEW_NOT_FOUND));

        UserInterviewReply reply =
            UserInterviewReply.builder()
                .user(user)
                .interview(interview)
                .userReply(dto.userReply())
                .aiFeedback(feedback.comment())
                .nth(interviewAllReplyReqDto.nth())
                .build();

        replies.add(reply);
      }
      replyRepository.saveAll(replies);

      return feedbacks;

    } catch (Exception e) {
      log.warn("면접 피드백 요청 실패 - userId: {}, reason: {}", userId, e.getMessage(), e);
      return Collections.emptyList();
    }
  }

  public InterviewAllSubIdResDto getInterviewAllSubId(Long userId) {
    List<SubjectInfo> allSubjectIds = subjectRepository.findAllSubjectInfos();
    List<SubjectInfo> existingSubjectIds = roadmapRepository.findSubjectInfosByUserId(userId);

    Set<Long> existingSubIdSet =
        existingSubjectIds.stream().map(SubjectInfo::subId).collect(Collectors.toSet());

    // 중복되는 과목들
    Map<Long, Long> aliasMap =
        Map.of(
            3L, 41L,
            6L, 37L,
            7L, 38L);

    List<SubjectInfo> missingSubjectIds =
        allSubjectIds.stream()
            .filter(s -> !existingSubIdSet.contains(s.subId()))
            .map(s -> new SubjectInfo(s.subId(), s.subjectNm(), false))
            .collect(Collectors.toCollection(ArrayList::new));

    // 중복되는 과목 삭제
    for (Map.Entry<Long, Long> entry : aliasMap.entrySet()) {
      Long a = entry.getKey();
      Long b = entry.getValue();
      if (existingSubIdSet.contains(a)) {
        missingSubjectIds.removeIf(s -> s.subId().equals(b));
      } else if (existingSubIdSet.contains(b)) {
        missingSubjectIds.removeIf(s -> s.subId().equals(a));
      }
    }
    return new InterviewAllSubIdResDto(existingSubjectIds, missingSubjectIds);
  }

  @Transactional
  public void savePrivacy(Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new CustomException(StatusCode.USER_NOT_FOUND));
    user.savePrivacyStatus();
  }
}

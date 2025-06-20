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
                    .summary(r.getSummary())
                    .modelAnswer(r.getModelAnswer())
                    .keyword(r.getKeyword())
                    .build())
        .toList();
  }

  public List<InterviewFeedbackResDto> saveReplyAndRequestFeedback(
      Long userId, InterviewAllReplyReqDto interviewAllReplyReqDto) {

    // 1. 트랜잭션 안에서 필요한 데이터 미리 조회
    User user = getUser(userId);
    Map<Long, Interview> interviewMap = getInterviews(interviewAllReplyReqDto.answers());

    // 2. 트랜잭션 종료 → 이제 느린 작업 가능
    List<InterviewFeedbackResDto> feedbacks =
        aiClient.getInterviewFeedback(userId, interviewAllReplyReqDto.answers());

    // 3. 응답 결과 → 트랜잭션 시작 후 저장
    saveReplies(user, interviewMap, interviewAllReplyReqDto, feedbacks);

    return feedbacks;
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

  @Transactional
  public User getUser(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new CustomException(StatusCode.USER_NOT_FOUND));
  }

  @Transactional
  public Map<Long, Interview> getInterviews(List<AiFeedbackReqDto> answers) {
    List<Long> ids = answers.stream().map(AiFeedbackReqDto::interviewId).toList();
    return interviewRepository.findAllById(ids).stream()
        .collect(Collectors.toMap(Interview::getInterviewId, i -> i));
  }

  @Transactional
  public void saveReplies(
      User user,
      Map<Long, Interview> interviews,
      InterviewAllReplyReqDto dto,
      List<InterviewFeedbackResDto> feedbacks) {

    List<UserInterviewReply> replies = new ArrayList<>();

    for (int i = 0; i < feedbacks.size(); i++) {
      AiFeedbackReqDto req = dto.answers().get(i);
      InterviewFeedbackResDto res = feedbacks.get(i);

      replies.add(
          UserInterviewReply.builder()
              .user(user)
              .interview(interviews.get(req.interviewId()))
              .userReply(req.userReply())
              .nth(dto.nth())
              .aiFeedback(res.comment())
              .modelAnswer(res.modelAnswer())
              .summary(res.conceptSummary())
              .keyword(String.join(",", res.recommendKeywords()))
              .build());
    }

    replyRepository.saveAll(replies);
  }
}

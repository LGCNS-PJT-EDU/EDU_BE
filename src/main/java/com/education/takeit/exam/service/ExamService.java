package com.education.takeit.exam.service;

import com.education.takeit.exam.dto.*;
import com.education.takeit.exam.entity.Exam;
import com.education.takeit.exam.enums.Difficulty;
import com.education.takeit.exam.repository.ExamRepository;
import com.education.takeit.global.client.AIClient;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.kafka.dto.FeedbackRequestDto;
import com.education.takeit.kafka.producer.FeedbackKafkaProducer;
import com.education.takeit.roadmap.entity.Roadmap;
import com.education.takeit.roadmap.entity.Subject;
import com.education.takeit.roadmap.repository.RoadmapRepository;
import com.education.takeit.roadmap.repository.SubjectRepository;
import com.education.takeit.solution.entity.UserExamAnswer;
import com.education.takeit.solution.repository.UserExamAnswerRepository;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ExamService {

  private final AIClient aiClient;
  private final RoadmapRepository roadmapRepository;
  private final ExamLevelCalculator examLevelCalculator;
  private final FeedbackKafkaProducer feedbackKafkaProducer;
  private final UserRepository userRepository;
  private final SubjectRepository subjectRepository;
  private final ExamRepository examRepository;
  private final UserExamAnswerRepository userExamAnswerRepository;

  /**
   * 사전 평가 문제 조회
   *
   * @param userId
   * @param subjectId
   * @return
   */
  public List<ExamResDto> findPreExam(Long userId, Long subjectId) {
    List<ExamResDto> result = aiClient.getPreExam(userId, subjectId);
    if (result.isEmpty()) {
      throw new CustomException(StatusCode.EMPTY_RESULT);
    }
    return result;
  }

  /**
   * 사전 평가 결과 저장
   *
   * @param userId
   * @param examAnswerRes
   * @return
   */
  public ResponseEntity<Void> submitPreExam(Long userId, ExamAnswerResDto examAnswerRes) {
    List<ExamAnswerDto> answers = examAnswerRes.answers();

    Roadmap roadmap = roadmapRepository.findByRoadmapId(examAnswerRes.roadmapId());
    if (roadmap == null) {
      throw new CustomException(StatusCode.NOT_FOUND_ROADMAP);
    }
    SubjectResultDto subject = calculateSubjectResultForPre(examAnswerRes);
    List<ChapterResultDto> chapters = calculateChapterResults(answers);

    ExamResultDto result = new ExamResultDto(userId, subject, chapters, answers);
    try {
      aiClient.postPreExam(userId, result);
    } catch (RestClientException e) {
      log.warn("사전 평가 결과 전송 실패: {}", e.getMessage());
    }

    roadmap.setLevel(subject.level());
    roadmap.setPreSubmitCount(roadmap.getPreSubmitCount() + 1);

    roadmapRepository.save(roadmap);

    // 결과 저장 성공 직후
    Long subjectId = roadmap.getSubject().getSubId();
    String type = "pre";
    int nth = roadmap.getPreSubmitCount();
    FeedbackRequestDto event = new FeedbackRequestDto(userId, subjectId, type, nth);
    feedbackKafkaProducer.publish(event);

    saveUserExamAnswer(userId, answers, true, subject.submitCnt(), examAnswerRes.subjectId());
    return ResponseEntity.noContent().build();
  }

  /**
   * 사후 평가 문제 조회
   *
   * @param userId
   * @param subjectId
   * @return
   */
  public List<ExamResDto> findPostExam(Long userId, Long subjectId) {
    List<ExamResDto> result = aiClient.getPostExam(userId, subjectId);
    if (result.isEmpty()) {
      throw new CustomException(StatusCode.EMPTY_RESULT);
    }
    return result;
  }

  /**
   * 사후 평가 결과 저장
   *
   * @param userId
   * @param examAnswerRes
   * @return
   */
  public ResponseEntity<Void> submitPostExam(Long userId, ExamAnswerResDto examAnswerRes) {
    List<ExamAnswerDto> answers = examAnswerRes.answers();

    Roadmap roadmap = roadmapRepository.findByRoadmapId(examAnswerRes.roadmapId());
    if (roadmap == null) {
      throw new CustomException(StatusCode.NOT_FOUND_ROADMAP);
    }

    SubjectResultDto subject = calculateSubjectResultForPost(roadmap, examAnswerRes);
    List<ChapterResultDto> chapters = calculateChapterResults(answers);

    ExamResultDto result = new ExamResultDto(userId, subject, chapters, answers);

    try {
      aiClient.postPostExam(userId, result);
    } catch (RestClientException e) {
      log.warn("사후 평가 결과 전송 실패: {}", e.getMessage());
    }

    roadmap.setLevel(subject.level());
    roadmap.setPostSubmitCount(roadmap.getPostSubmitCount() + 1);
    if (!roadmap.isComplete()) roadmap.setComplete(true);

    roadmapRepository.save(roadmap);

    // 결과 저장 성공 직후
    Long subjectId = roadmap.getSubject().getSubId();
    String type = "post";
    int nth = roadmap.getPostSubmitCount();
    FeedbackRequestDto event = new FeedbackRequestDto(userId, subjectId, type, nth);
    feedbackKafkaProducer.publish(event);

    saveUserExamAnswer(userId, answers, false, subject.submitCnt(), examAnswerRes.subjectId());
    return ResponseEntity.noContent().build();
  }

  /**
   * 과목별 사전 평가 집계
   *
   * @param examAnswerRes
   * @return
   */
  private SubjectResultDto calculateSubjectResultForPre(ExamAnswerResDto examAnswerRes) {
    List<ExamAnswerDto> answers = examAnswerRes.answers();
    Long subjectId = examAnswerRes.subjectId();
    String startDate = examAnswerRes.startDate();
    Long duration = examAnswerRes.duration();
    int submitCnt = examAnswerRes.submitCnt() + 1;
    int level = calculatePreLevel(answers);
    int cnt = (int) answers.stream().filter(ExamAnswerDto::answerTF).count();
    int totalCnt = answers.size();

    return new SubjectResultDto(subjectId, startDate, duration, submitCnt, level, cnt, totalCnt);
  }

  /**
   * 과목별 사후 평가 집계
   *
   * @param examAnswerRes
   * @return
   */
  private SubjectResultDto calculateSubjectResultForPost(
      Roadmap roadmap, ExamAnswerResDto examAnswerRes) {
    List<ExamAnswerDto> answers = examAnswerRes.answers();
    Long subjectId = examAnswerRes.subjectId();
    String startDate = examAnswerRes.startDate();
    Long duration = examAnswerRes.duration();
    int submitCnt = examAnswerRes.submitCnt() + 1;
    int level = calculatePostLevel(roadmap, answers);
    int cnt = (int) answers.stream().filter(ExamAnswerDto::answerTF).count();
    int totalCnt = answers.size();

    return new SubjectResultDto(subjectId, startDate, duration, submitCnt, level, cnt, totalCnt);
  }

  /**
   * 과목별 사전 평가 레벨 집계
   *
   * @param answers
   * @return
   */
  private int calculatePreLevel(List<ExamAnswerDto> answers) {
    int score =
        answers.stream()
            .mapToInt(a -> examLevelCalculator.calculateScoreByDifficulty(a, false))
            .sum();

    if (score <= 4) return 1;
    if (score <= 8) return 2;
    if (score <= 12) return 3;
    if (score <= 16) return 4;
    return 5;
  }

  /**
   * 과목별 사후 평가 레벨 집계
   *
   * @param roadmap
   * @param answers
   * @return
   */
  private int calculatePostLevel(Roadmap roadmap, List<ExamAnswerDto> answers) {
    int scorePercent = examLevelCalculator.calculateScorePercent(answers);
    int levelDelta = examLevelCalculator.calculateLevelDelta(scorePercent);
    return examLevelCalculator.calculateNewLevel(roadmap.getLevel(), levelDelta);
  }

  /**
   * 단원별 평가 집계
   *
   * @param examAnswerRes
   * @return
   */
  private List<ChapterResultDto> calculateChapterResults(List<ExamAnswerDto> examAnswerRes) {
    return examAnswerRes.stream()
        .collect(Collectors.groupingBy(ExamAnswerDto::chapterNum))
        .entrySet()
        .stream()
        .map(
            entry -> {
              List<ExamAnswerDto> chapterAnswers = entry.getValue();
              String chapterName = chapterAnswers.getFirst().chapterName();
              boolean weakness =
                  chapterAnswers.stream()
                      .anyMatch(
                          dto ->
                              Difficulty.fromLabel(dto.difficulty()) == Difficulty.EASY
                                  && !dto.answerTF());
              int cnt = (int) chapterAnswers.stream().filter(ExamAnswerDto::answerTF).count();
              int totalCnt = chapterAnswers.size();
              return new ChapterResultDto(entry.getKey(), chapterName, weakness, cnt, totalCnt);
            })
        .toList();
  }

  private void saveUserExamAnswer(
      Long userId, List<ExamAnswerDto> answers, boolean isPre, int nth, Long subjectId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new CustomException(StatusCode.USER_NOT_FOUND));
    Subject subject =
        subjectRepository
            .findById(subjectId)
            .orElseThrow(() -> new CustomException(StatusCode.SUBJECT_NOT_FOUND));

    for (ExamAnswerDto answer : answers) {
      Exam exam =
          examRepository
              .findById(answer.examId())
              .orElseThrow(() -> new CustomException(StatusCode.EXAM_NOT_FOUND));

      UserExamAnswer entity =
          UserExamAnswer.builder()
              .user(user)
              .subject(subject)
              .exam(exam)
              .userAnswer(answer.userAnswer())
              .isPre(isPre)
              .nth(nth)
              .build();

      userExamAnswerRepository.save(entity);
    }
  }
}

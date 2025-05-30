package com.education.takeit.solution.service;

import com.education.takeit.exam.dto.ExamAnswerDto;
import com.education.takeit.exam.entity.Exam;
import com.education.takeit.exam.repository.ExamRepository;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.roadmap.entity.Roadmap;
import com.education.takeit.roadmap.entity.Subject;
import com.education.takeit.roadmap.repository.RoadmapRepository;
import com.education.takeit.solution.dto.SolutionResDto;
import com.education.takeit.solution.entity.UserExamAnswer;
import com.education.takeit.solution.repository.UserExamAnswerRepository;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SolutionService {
  private final UserExamAnswerRepository userExamAnswerRepository;
  private final UserRepository userRepository;
  private final ExamRepository examRepository;
  private final RoadmapRepository roadmapRepository;

  // 해설 조회
  public List<SolutionResDto> findAllUserSolutions(Long userId, Long subjectId) {
    List<UserExamAnswer> solutionList =
        userExamAnswerRepository.findByUser_UserIdAndExam_Subject_SubId(userId, subjectId);

    if (solutionList.isEmpty()) {
      throw new CustomException(StatusCode.NOT_FOUND_SOLUTION);
    }

    // TODO : convert로 메소드 분리
    return solutionList.stream()
        .map(
            solution -> {
              Exam exam = solution.getExam();
              Subject subject = exam.getSubject();
              return new SolutionResDto(
                  solution.isPre(), // 사전 사후 여부
                  solution.getNth(), // 회차 정보
                  subject.getSubNm(), // 과목 이름
                  exam.getExamContent(), // 문제 내용
                  exam.getOption1(), // 보기 1
                  exam.getOption2(), // 보기 2
                  exam.getOption3(), // 보기 3
                  exam.getOption4(), // 보기 4
                  exam.getExamAnswer(), // 정답
                  solution.getUserAnswer(), // 사용자 선택
                  exam.getSolution(), // 해설
                  exam.getExamLevel() // 난이도
                  );
            })
        .collect(Collectors.toList());
  }

  public void saveAllUserSolutions(Long userId, List<ExamAnswerDto> answers, boolean isPre) {
    User user = userRepository.findById(userId).orElseThrow();

    for (ExamAnswerDto answer : answers) {
      Exam exam = examRepository.findByExamId(answer.examId());
      Subject subject = exam.getSubject();
      if (subject == null) {
        throw new CustomException(StatusCode.SUBJECT_NOT_FOUND);
      }

      int nth;
      Roadmap roadmap = roadmapRepository.findBySubject_SubId(subject.getSubId());
      if (!isPre) {
        nth = roadmap.getPostSubmitCount() + 1;
      } else{
        nth = 1;
      }
      UserExamAnswer userExamAnswer =
              UserExamAnswer.builder()
                      .user(user)
                      .subject(subject)
                      .exam(exam)
                      .userAnswer(answer.userAnswer())
                      .isPre(isPre)
                      .nth(nth)
                      .build();
      userExamAnswerRepository.save(userExamAnswer);
    }
  }
}

package com.education.takeit.solution.repository;

import com.education.takeit.solution.entity.UserExamAnswer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserExamAnswerRepository extends JpaRepository<UserExamAnswer, Long> {
  List<UserExamAnswer> findByUser_UserIdAndExam_Subject_SubId(Long userId, Long examId);
}

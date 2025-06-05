package com.education.takeit.solution.repository;

import com.education.takeit.roadmap.entity.Subject;
import com.education.takeit.solution.entity.UserExamAnswer;
import com.education.takeit.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserExamAnswerRepository extends JpaRepository<UserExamAnswer, Long> {
  List<UserExamAnswer> findByUser_UserIdAndSubject_SubId(Long userId, Long examId);
  void deleteByUser_UserId(Long userId);

  int countByUserAndSubjectAndIsPre(User user, Subject subject, boolean isPre);
}

package com.education.takeit.feedback.repository;

import com.education.takeit.feedback.entity.Feedback;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
  void deleteByUser_UserId(Long userId);

  List<Feedback> findByUser_UserIdAndSubject_SubId(Long userId, Long subId);
}

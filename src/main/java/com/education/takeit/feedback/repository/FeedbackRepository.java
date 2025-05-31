package com.education.takeit.feedback.repository;

import com.education.takeit.feedback.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {}

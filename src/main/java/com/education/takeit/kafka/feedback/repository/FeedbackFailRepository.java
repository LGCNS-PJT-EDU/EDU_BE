package com.education.takeit.kafka.feedback.repository;

import com.education.takeit.kafka.feedback.entity.FeedbackFailLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackFailRepository extends JpaRepository<FeedbackFailLog, Long> {}

package com.education.takeit.kafka.feedback.repository;

import com.education.takeit.kafka.feedback.entity.FeedbackFailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FeedbackFailRepository extends JpaRepository<FeedbackFailLog, Long>, JpaSpecificationExecutor<FeedbackFailLog> {
}

package com.education.takeit.kafka.repository;

import com.education.takeit.kafka.entity.FeedbackFailLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackFailRepository extends JpaRepository<FeedbackFailLog, Long> {}

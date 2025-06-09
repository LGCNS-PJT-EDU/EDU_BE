package com.education.takeit.kafka.recommand.repository;

import com.education.takeit.kafka.recommand.entity.RecomFailLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecomFailRepository extends JpaRepository<RecomFailLog, Long> {}

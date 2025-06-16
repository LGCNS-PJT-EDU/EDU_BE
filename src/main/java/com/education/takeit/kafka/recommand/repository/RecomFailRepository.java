package com.education.takeit.kafka.recommand.repository;

import com.education.takeit.kafka.recommand.entity.RecomFailLog;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RecomFailRepository
    extends JpaRepository<RecomFailLog, Long>, JpaSpecificationExecutor<RecomFailLog> {
  List<RecomFailLog> findByCreatedDtBetween(LocalDateTime startDate, LocalDateTime endDate);
}

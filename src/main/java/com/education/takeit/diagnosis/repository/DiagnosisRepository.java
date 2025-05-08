package com.education.takeit.diagnosis.repository;

import com.education.takeit.diagnosis.entity.Diagnosis;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {

  @Query("SELECT d FROM Diagnosis d JOIN FETCH d.choices")
  List<Diagnosis> findAllWithChoices();
}

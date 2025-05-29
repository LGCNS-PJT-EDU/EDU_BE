package com.education.takeit.exam.repository;

import com.education.takeit.exam.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamRepository extends JpaRepository<Exam, Long> {
  Exam findByExamId(Long examId);
}

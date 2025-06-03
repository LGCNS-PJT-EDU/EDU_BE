package com.education.takeit.exam.repository;

import com.education.takeit.exam.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExamRepository extends JpaRepository<Exam, Long> {
  Optional<Exam> findByExamContent(String examContent);
}

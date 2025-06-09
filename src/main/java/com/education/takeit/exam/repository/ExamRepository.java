package com.education.takeit.exam.repository;

import com.education.takeit.exam.entity.Exam;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamRepository extends JpaRepository<Exam, Long> {
  Optional<Exam> findByExamContent(String examContent);
}

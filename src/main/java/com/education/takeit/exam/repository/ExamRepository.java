package com.education.takeit.exam.repository;

import com.education.takeit.admin.dto.AdminExamResDto;
import com.education.takeit.exam.entity.Exam;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ExamRepository extends JpaRepository<Exam, Long> {
  Optional<Exam> findByExamContentAndSubject_SubId(String examContent, Long subId);

  @Query(
      """
  SELECT new com.education.takeit.admin.dto.AdminExamResDto(
    e.examId,
    e.examContent,
    e.examAnswer,
    e.examLevel,
    e.option1,
    e.option2,
    e.option3,
    e.option4,
    e.solution,
    COUNT(uea),
    s.subNm
  )
  FROM Exam e
  JOIN e.subject s
  LEFT JOIN UserExamAnswer uea ON uea.exam = e
  WHERE (:subName IS NULL OR LOWER(s.subNm) LIKE LOWER(CONCAT('%', :subName, '%')))
    AND (:examContent IS NULL OR LOWER(e.examContent) LIKE LOWER(CONCAT('%', :examContent, '%')))
  GROUP BY e.examId, e.examContent, e.examAnswer, e.examLevel,
           e.option1, e.option2, e.option3, e.option4, e.solution, s.subNm
""")
  List<AdminExamResDto> findExamWithUserCountAndFilter(
      @Param("subName") String subName, @Param("examContent") String examContent);
}

package com.education.takeit.exam.entity;

import com.education.takeit.roadmap.entity.Subject;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Exam {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long examId;

  @Column(name = "exam_content", nullable = false, columnDefinition = "TEXT")
  private String examContent;

  @Column(name = "exam_answer", nullable = false)
  private int examAnswer;

  @Column(name = "exam_level", nullable = false)
  private String examLevel;

  @Column(name = "option1", nullable = false)
  private String option1;

  @Column(name = "option2", nullable = false)
  private String option2;

  @Column(name = "option3", nullable = false)
  private String option3;

  @Column(name = "option4", nullable = false)
  private String option4;

  @Column(name = "solution", nullable = false)
  private String solution;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sub_id", nullable = false)
  private Subject subject;
}

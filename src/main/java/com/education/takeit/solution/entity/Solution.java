package com.education.takeit.solution.entity;

import com.education.takeit.exam.entity.Exam;
import jakarta.persistence.*;

@Entity
public class Solution {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long solutionId;

  @Column(name = "user_answer", nullable = false)
  private int userAnswer;

  @Column(name = "solution_content", nullable = true)
  private String solutionContent;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "exam_id", nullable = false)
  private Exam exam;
}

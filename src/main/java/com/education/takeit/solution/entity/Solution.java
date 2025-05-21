package com.education.takeit.solution.entity;

import com.education.takeit.exam.entity.Exam;
import com.education.takeit.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Solution {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long solutionId;

  @Column(name = "user_answer", nullable = false)
  private int userAnswer;

  @Column(name = "solution_content", nullable = true)
  private String solutionContent;

  @Column(name = "is_pre", nullable = false)
  private boolean isPre;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "exam_id", nullable = false)
  private Exam exam;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
}

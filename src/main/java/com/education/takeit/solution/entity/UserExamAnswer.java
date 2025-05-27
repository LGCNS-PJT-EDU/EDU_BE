package com.education.takeit.solution.entity;

import com.education.takeit.exam.entity.Exam;
import com.education.takeit.roadmap.entity.Subject;
import com.education.takeit.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "user_exam_answer")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserExamAnswer {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long examAnswerId;

  @Column(name = "user_answer", nullable = false)
  private int userAnswer;

  @Column(name = "is_pre", nullable = false)
  private boolean isPre;

  @Column(name = "nth", nullable = false)
  private int nth;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "exam_id", nullable = false)
  private Exam exam;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sub_id", nullable = false)
  private Subject subject;
}

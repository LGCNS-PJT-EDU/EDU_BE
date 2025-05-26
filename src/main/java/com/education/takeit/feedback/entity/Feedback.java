package com.education.takeit.feedback.entity;

import com.education.takeit.roadmap.entity.Subject;
import com.education.takeit.user.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "feedback")
public class Feedback {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long feedbackId;

  @Column(name = "feedback_content", nullable = false)
  private String feedbackContent;

  @Column(name = "nth", nullable = false)
  private int nth;

  @Column(name = "is_pre", nullable = false)
  private boolean isPre;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sub_id", nullable = false)
  private Subject subject;
}

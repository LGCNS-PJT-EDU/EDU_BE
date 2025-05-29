package com.education.takeit.feedback.entity;

import com.education.takeit.roadmap.entity.Subject;
import com.education.takeit.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "feedback")
@Getter
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

  @Column(name = "strenth", nullable = false, columnDefinition = "LONGTEXT")
  private String strenth;

  @Column(name = "weakness", nullable = false, columnDefinition = "LONGTEXT")
  private String weakness;

  public Feedback(
      String feedbackContent,
      int nth,
      boolean isPre,
      User user,
      Subject subject,
      String strenth,
      String weakness) {
    this.feedbackContent = feedbackContent;
    this.nth = nth;
    this.isPre = isPre;
    this.user = user;
    this.subject = subject;
    this.strenth = strenth;
    this.weakness = weakness;
  }
}

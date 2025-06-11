package com.education.takeit.feedback.entity;

import com.education.takeit.roadmap.entity.Subject;
import com.education.takeit.user.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "feedback")
@Getter
@NoArgsConstructor
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
  private String strength;

  @Column(name = "weakness", nullable = false, columnDefinition = "LONGTEXT")
  private String weakness;

  @Column(name = "scores", nullable = false, columnDefinition = "LONGTEXT")
  private String scores;

  @Column(name = "created_at", nullable = false)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  @JsonProperty("date")
  private LocalDate createdAt = LocalDate.now(); // 기본값: 현재 날짜

  public Feedback(
      String feedbackContent,
      int nth,
      boolean isPre,
      User user,
      Subject subject,
      String strength,
      String weakness,
      String scores) {
    this.feedbackContent = feedbackContent;
    this.nth = nth;
    this.isPre = isPre;
    this.user = user;
    this.subject = subject;
    this.strength = strength;
    this.weakness = weakness;
    this.scores = scores;
  }
}

package com.education.takeit.interview.entity;

import com.education.takeit.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_interview_reply")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UserInterviewReply {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long replyId;

  @Column(name = "user_reply", nullable = false, columnDefinition = "TEXT")
  private String userReply;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "interview_id", nullable = false)
  private Interview interview;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "ai_feedback", nullable = false, columnDefinition = "TEXT")
  private String aiFeedback;

  @Column(name = "nth", nullable = false)
  private Integer nth;
}

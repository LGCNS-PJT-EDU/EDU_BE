package com.education.takeit.interview.entity;

import com.education.takeit.user.entity.User;
import jakarta.persistence.*;

@Entity
public class UserInterviewReply {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long replyId;

  @Column(name = "user_reply", nullable = false)
  private String userReply;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "interview_id", nullable = false)
  private Interview interviewId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
}

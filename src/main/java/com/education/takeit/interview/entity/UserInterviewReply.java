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

  @Column(name = "interview_id", nullable = false)
  private Long interviewId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
}

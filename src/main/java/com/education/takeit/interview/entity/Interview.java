package com.education.takeit.interview.entity;

import com.education.takeit.roadmap.entity.Subject;
import jakarta.persistence.*;

@Entity
public class Interview {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long interviewId;

  @Column(name = "interview_id", nullable = false)
  private String interviewContent;

  @Column(name = "interview_answer", nullable = false)
  private String interviewAnswer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sub_id", nullable = false)
  private Subject subject;
}

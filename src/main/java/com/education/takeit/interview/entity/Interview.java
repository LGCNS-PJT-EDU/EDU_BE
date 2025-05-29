package com.education.takeit.interview.entity;

import com.education.takeit.roadmap.entity.Subject;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "interview")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Interview {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long interviewId;

  @Column(name = "interview_content", nullable = false, columnDefinition = "TEXT")
  private String interviewContent;

  @Column(name = "interview_answer", nullable = false, columnDefinition = "TEXT")
  private String interviewAnswer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sub_id", nullable = false)
  private Subject subject;
}

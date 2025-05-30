package com.education.takeit.kafka.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "feedback_fail_log")
@EntityListeners(AuditingEntityListener.class)
public class FeedbackFailLog {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "subject_id", nullable = false)
  private Long subjectId;

  @Column(name = "type", nullable = false)
  private String type;

  @Column(name = "nth", nullable = false)
  private Integer nth;

  @Column(name = "error_code", nullable = false)
  private String errorCode;

  @Column(name = "error_message", nullable = false)
  private String errorMessage;

  @CreatedDate
  @Column(name = "created_dt", updatable = false)
  private LocalDateTime createdDt;
}

package com.education.takeit.kafka.feedback.entity;

import com.education.takeit.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "feedback_fail_log")
@EntityListeners(AuditingEntityListener.class)
public class FeedbackFailLog {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

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

  // 새로 추가된 retry 필드
  @Column(name = "retry", nullable = false)
  @Builder.Default
  private Boolean retry = Boolean.FALSE;

}

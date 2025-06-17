package com.education.takeit.kafka.recommend.entity;

import com.education.takeit.user.entity.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recom_fail_log")
@EntityListeners(AuditingEntityListener.class)
public class RecomFailLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "subject_id", nullable = false)
  private Long subjectId;

  @Column(name = "error_code", nullable = false)
  private String errorCode;

  @Column(name = "error_message", nullable = false)
  private String errorMessage;

  @CreatedDate
  @Column(name = "created_dt", updatable = false, nullable = false)
  private LocalDateTime createdDt;

  @Column(name = "retry", nullable = false)
  @Builder.Default
  private Boolean retry = Boolean.FALSE;

  public void markRetry() {
    this.retry = true;
  }
}

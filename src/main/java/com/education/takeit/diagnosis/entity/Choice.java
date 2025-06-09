package com.education.takeit.diagnosis.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "choice")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Choice {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long choiceId;

  @Column(nullable = false)
  private int choiceNum;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String choice;

  @Column(name = "\"value\"", nullable = false, length = 255)
  private String value;

  @CreatedDate
  @Column(name = "created_dt", updatable = false)
  @Builder.Default
  private LocalDateTime createdDt = LocalDateTime.now();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "diagnosis_id", nullable = false)
  private Diagnosis diagnosis;
}

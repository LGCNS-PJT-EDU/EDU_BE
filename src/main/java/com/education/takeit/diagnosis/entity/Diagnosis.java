package com.education.takeit.diagnosis.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "diagnosis")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Diagnosis {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long diagnosisId;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String question;

  @Column(nullable = false, length = 50)
  private String questionType;

  @CreatedDate
  @Column(name = "created_dt", updatable = false)
  @Builder.Default
  private LocalDateTime createdDt = LocalDateTime.now();

  @Builder.Default
  @OneToMany(
      mappedBy = "diagnosis",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private List<Choice> choices = new ArrayList<>();
}

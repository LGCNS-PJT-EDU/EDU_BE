package com.education.takeit.roadmap.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "subject")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subject {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long subId;

  @Column(name = "sub_nm", nullable = false)
  private String subNm;

  @Column(name = "sub_type", nullable = false)
  private String subType;

  @Column(name = "sub_essential", nullable = false, length = 1)
  private String subEssential;

  @Column(name = "base_sub_order", nullable = false)
  private Integer baseSubOrder;

  @Lob private String subOverview;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "track_id", nullable = false)
  private Track track;
}

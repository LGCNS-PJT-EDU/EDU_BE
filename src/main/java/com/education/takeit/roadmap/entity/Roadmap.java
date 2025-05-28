package com.education.takeit.roadmap.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "roadmap")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Roadmap {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long roadmapId;

  @Column(name = "order_sub", nullable = false)
  private Integer orderSub;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sub_id", nullable = false)
  private Subject subject;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "roadmap_management_id", nullable = false)
  private RoadmapManagement roadmapManagement;

  @Column(name = "is_complete", nullable = false)
  private boolean isComplete;

  // 사후평가 완료 여부

  @Column(name = "pre_submit_count", nullable = false)
  private int preSubmitCount;

  @Column(name = "post_submit_count", nullable = false)
  private int postSubmitCount;

  @Builder.Default
  @Column(name = "level", nullable = false, columnDefinition = "INT DEFAULT 0")
  private Integer level = 0;
}

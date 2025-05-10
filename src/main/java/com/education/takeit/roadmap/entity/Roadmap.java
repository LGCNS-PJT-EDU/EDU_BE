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

  @Column(nullable = false)
  private Integer orderSub;

  @Column(nullable = false)
  private Long userId; // 참조용 ID만 저장 (users 테이블과 매핑 필요 시 @ManyToOne 적용)

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sub_id", nullable = false)
  private Subject subject;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "roadmap_management_id", nullable = false)
  private RoadmapManagement roadmapManagement;

  @Column(nullable = false)
  private boolean isComplete;

  @Column
  private int preSubmitCount;

  @Column
  private int postSubmitCount;
}

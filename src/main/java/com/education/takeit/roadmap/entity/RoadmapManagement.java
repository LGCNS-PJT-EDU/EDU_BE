package com.education.takeit.roadmap.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "roadmap_management")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoadmapManagement {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long roadmapManagementId;

  @Column(name = "roadmap_nm")
  private String roadmapNm;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @UpdateTimestamp
  @Column(name = "roadmap_timestamp")
  private LocalDateTime roadmapTimestamp;

}

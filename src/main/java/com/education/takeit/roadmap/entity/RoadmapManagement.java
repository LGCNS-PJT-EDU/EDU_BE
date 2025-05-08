package com.education.takeit.roadmap.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
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

  private String roadmapNm;

  @Column(
      name = "roadmap_timestamp",
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
  private LocalDateTime roadmapTimestamp;

  @Column(name = "lecture_amount")
  private Integer lectureAmount; // 응답 (0 ~ 4)

  @Column(name = "price_level")
  private Integer priceLevel; // 응답 (0 ~ 5)

  @Column(name = "likes_books")
  private Boolean likesBooks; // Y: true, N: false
}

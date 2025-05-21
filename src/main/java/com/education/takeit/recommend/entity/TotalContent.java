package com.education.takeit.recommend.entity;

import com.education.takeit.roadmap.entity.LectureAmount;
import com.education.takeit.roadmap.entity.PriceLevel;
import com.education.takeit.roadmap.entity.Subject;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "total_content")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TotalContent {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long contentId;

  @Column(name = "content_title", nullable = false)
  private String contentTitle;

  @Column(name = "content_url", nullable = false)
  private String contentUrl;

  @Column(name = "content_type", nullable = false)
  private String contentType;

  @Column(name = "content_platform", nullable = false)
  private String contentPlatform;

  @Enumerated(EnumType.STRING)
  @Column(name = "content_duration", nullable = false)
  private LectureAmount contentDuration;

  @Column(name = "content_level", nullable = false)
  private String contentLevel;

  @Enumerated(EnumType.STRING)
  @Column(name = "content_price", nullable = false)
  private PriceLevel contentPrice;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sub_id", nullable = false)
  private Subject subject;
}

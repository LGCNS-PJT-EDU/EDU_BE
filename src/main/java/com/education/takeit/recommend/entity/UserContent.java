package com.education.takeit.recommend.entity;

import com.education.takeit.roadmap.entity.Subject;
import com.education.takeit.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_content")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserContent {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userContentId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "total_content_id", nullable = false)
  private TotalContent totalContent;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sub_id", nullable = false)
  private Subject subject;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "is_ai_recommended", nullable = false)
  private Boolean isAiRecommended;
}

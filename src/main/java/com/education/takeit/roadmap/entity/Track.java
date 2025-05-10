package com.education.takeit.roadmap.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "track")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Track {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long trackId;

  @Column(name = "track_nm", nullable = false, unique = true)
  private String trackNm;
}

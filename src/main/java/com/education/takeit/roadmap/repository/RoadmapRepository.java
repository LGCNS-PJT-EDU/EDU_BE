package com.education.takeit.roadmap.repository;

import com.education.takeit.roadmap.entity.Roadmap;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {
  @EntityGraph(attributePaths = {"subject", "roadmapManagement"})
  List<Roadmap> findByUserId(Long userId);

  List<Roadmap> findByRoadmapManagement_RoadmapManagementId(Long id);

  Roadmap findByUserIdAndSubject_SubId(Long userId, Long subjectId);
}

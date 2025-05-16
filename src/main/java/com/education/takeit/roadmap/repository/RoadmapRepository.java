package com.education.takeit.roadmap.repository;

import com.education.takeit.roadmap.entity.Roadmap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {
  List<Roadmap> findByUserId(Long userId);

  List<Roadmap> findAllByUserId(Long userId);

  List<Roadmap> findByRoadmapManagement_RoadmapManagementId(Long id);

  Roadmap findByUserIdAndSubject_SubId(Long userId, Long subjectId);

  Roadmap findByRoadmapId(Long roadmapId);

}

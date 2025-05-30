package com.education.takeit.roadmap.repository;

import com.education.takeit.roadmap.entity.Roadmap;
import com.education.takeit.roadmap.entity.RoadmapManagement;
import com.education.takeit.roadmap.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {
  List<Roadmap> findByRoadmapManagement_RoadmapManagementId(Long id);

  Roadmap findByRoadmapId(Long roadmapId);

  Roadmap findBySubjectAndRoadmapManagement(Subject subject, RoadmapManagement roadmapManagement);

  Roadmap findBySubject_SubId(Long subId);
}

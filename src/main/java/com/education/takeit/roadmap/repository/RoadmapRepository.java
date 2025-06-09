package com.education.takeit.roadmap.repository;

import com.education.takeit.interview.dto.SubjectInfo;
import com.education.takeit.roadmap.entity.Roadmap;
import com.education.takeit.roadmap.entity.RoadmapManagement;
import com.education.takeit.roadmap.entity.Subject;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {
  List<Roadmap> findByRoadmapManagement_RoadmapManagementId(Long id);

  Roadmap findByRoadmapId(Long roadmapId);

  Roadmap findBySubjectAndRoadmapManagement(Subject subject, RoadmapManagement roadmapManagement);

  @Query(
      """
    SELECT new com.education.takeit.interview.dto.SubjectInfo(s.subId, s.subNm)
    FROM Roadmap r
    JOIN r.subject s
    WHERE r.roadmapManagement.userId = :userId
""")
  List<SubjectInfo> findSubjectInfosByUserId(@Param("userId") Long userId);
}

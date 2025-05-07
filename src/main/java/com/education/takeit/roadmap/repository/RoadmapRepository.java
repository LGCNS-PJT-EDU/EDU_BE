package com.education.takeit.roadmap.repository;

import com.education.takeit.roadmap.entity.Roadmap;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {
  List<Roadmap> findByUserId(Long userId);
  List<Roadmap> findAllByUserId(Long userId);
}

package com.education.takeit.roadmap.repository;

import com.education.takeit.roadmap.entity.Roadmap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {
}

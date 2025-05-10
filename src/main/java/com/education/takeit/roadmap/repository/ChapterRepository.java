package com.education.takeit.roadmap.repository;

import com.education.takeit.roadmap.entity.Chapter;
import com.education.takeit.roadmap.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    List<Chapter> findBySubject(Subject subject);
}

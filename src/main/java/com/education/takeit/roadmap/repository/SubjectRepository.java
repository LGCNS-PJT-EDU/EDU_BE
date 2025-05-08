package com.education.takeit.roadmap.repository;

import com.education.takeit.roadmap.entity.Subject;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
  List<Subject> findBySubTypeAndSubEssential(String subType, String subEssential);
}

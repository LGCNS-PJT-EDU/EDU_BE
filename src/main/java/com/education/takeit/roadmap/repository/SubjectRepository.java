package com.education.takeit.roadmap.repository;

import com.education.takeit.interview.dto.SubjectInfo;
import com.education.takeit.roadmap.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
  List<Subject> findBySubTypeAndSubEssential(String subType, String subEssential);

  Optional<Subject> findBySubId(Long subId);

  @Query("SELECT new com.education.takeit.interview.dto.SubjectInfo(s.subId, s.subNm) FROM Subject s")
  List<SubjectInfo> findAllSubjectInfos();
}

package com.education.takeit.roadmap.repository;

import com.education.takeit.admin.dto.AdminSubjectResDto;
import com.education.takeit.interview.dto.SubjectInfo;
import com.education.takeit.roadmap.entity.Subject;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
  List<Subject> findBySubTypeAndSubEssential(String subType, String subEssential);

  Optional<Subject> findBySubId(Long subId);

  @Query(
      "SELECT new com.education.takeit.interview.dto.SubjectInfo(s.subId, s.subNm, false) FROM Subject s")
  List<SubjectInfo> findAllSubjectInfos();

  @Query(
          "SELECT new com.education.takeit.admin.dto.AdminSubjectResDto(" +
                  "s.subId, s.subNm, s.subType, s.subEssential, s.baseSubOrder, COUNT(r)) " +
                  "FROM Subject s LEFT JOIN Roadmap r ON r.subject = s " +
                  "WHERE (:keyword IS NULL OR LOWER(s.subNm) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                  "GROUP BY s.subId, s.subNm, s.subType, s.subEssential, s.baseSubOrder " +
                  "ORDER BY " +
                  "CASE WHEN :sortBy = 'count' THEN COUNT(r) END DESC, " +
                  "CASE WHEN :sortBy = 'name' THEN s.subNm END ASC," +
                  "CASE WHEN :sortBy = 'id' THEN s.subId END ASC"
  )
  Page<AdminSubjectResDto> findSubjectsWithAssignmentCount(
          @org.springframework.data.repository.query.Param("keyword") String keyword,
          @org.springframework.data.repository.query.Param("sortBy") String sortBy,
          Pageable pageable
  );
}

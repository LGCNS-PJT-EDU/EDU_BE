package com.education.takeit.roadmap.repository;

import com.education.takeit.roadmap.entity.Subject;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

  @Query("SELECT s FROM Subject s JOIN FETCH s.track t WHERE s.subId IN :Ids")
  List<Subject> findSubjectsByIds(List<Long> ids);

  List<Subject> findBySubTypeAndSubEssential(String subType, String subEssential);
}

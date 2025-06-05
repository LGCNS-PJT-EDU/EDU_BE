package com.education.takeit.interview.repository;

import com.education.takeit.interview.entity.Interview;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
  List<Interview> findBySubject_SubIdIn(List<Long> subjectIds);
}

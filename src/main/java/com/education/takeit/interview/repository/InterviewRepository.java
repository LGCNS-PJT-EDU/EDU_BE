package com.education.takeit.interview.repository;

import com.education.takeit.interview.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewRepository extends JpaRepository<Interview,Long> {
    List<Interview> findBySubjectId(Long subjectId);
}

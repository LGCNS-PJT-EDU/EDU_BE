package com.education.takeit.solution.repository;

import com.education.takeit.solution.entity.Solution;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolutionRepository extends JpaRepository<Solution, Long> {
  List<Solution> findAllByUser_UserId(long userId);
}

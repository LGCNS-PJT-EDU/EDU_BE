package com.education.takeit.recommend.repository;

import com.education.takeit.recommend.entity.UserContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserContentRepository extends JpaRepository<UserContent, Long> {

  @Query("SELECT uc FROM UserContent uc JOIN FETCH uc.totalContent WHERE uc.user.userId = :userId")
  List<UserContent> findByUserIdWithContent(@Param("userId") Long userId);
}

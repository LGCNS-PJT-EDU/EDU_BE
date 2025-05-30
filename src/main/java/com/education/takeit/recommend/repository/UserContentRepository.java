package com.education.takeit.recommend.repository;

import com.education.takeit.recommend.entity.UserContent;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserContentRepository extends JpaRepository<UserContent, Long> {

  @Query("SELECT uc FROM UserContent uc JOIN FETCH uc.totalContent WHERE uc.user.userId = :userId")
  List<UserContent> findByUserIdWithContent(@Param("userId") Long userId);

  List<UserContent> findByUser_UserIdAndSubject_SubjectId(Long userId, Long subjectId);




}

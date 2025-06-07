package com.education.takeit.interview.repository;

import com.education.takeit.interview.entity.UserInterviewReply;
import java.util.List;
import java.util.Optional;

import com.education.takeit.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserInterviewReplyRepository extends JpaRepository<UserInterviewReply, Long> {
  List<UserInterviewReply> findByUser_UserId(Long userId);

  @Query("SELECT MAX(r.nth) FROM UserInterviewReply r WHERE r.user.userId=:userId")
  Optional<Integer> findMaxNthByUserId(@Param("userId") Long userId);

}

package com.education.takeit.interview.repository;

import com.education.takeit.interview.entity.UserInterviewReply;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInterviewReplyRepository extends JpaRepository<UserInterviewReply, Long> {
  List<UserInterviewReply> findByUser_UserId(Long userId);
}

package com.education.takeit.interview.repository;

import com.education.takeit.interview.entity.UserInterviewReply;
import com.education.takeit.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserInterviewReplyRepository extends JpaRepository<UserInterviewReply, Long> {
    List<UserInterviewReply> findByUser_UserId(Long userId);
}


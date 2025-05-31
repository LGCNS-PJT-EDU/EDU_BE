package com.education.takeit.chat.repository;

import com.education.takeit.chat.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findAllByUserId(Long userId);
}

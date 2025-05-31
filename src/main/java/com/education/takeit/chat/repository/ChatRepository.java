package com.education.takeit.chat.repository;

import com.education.takeit.chat.entity.Chat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {
  List<Chat> findAllByUserId(Long userId);
}

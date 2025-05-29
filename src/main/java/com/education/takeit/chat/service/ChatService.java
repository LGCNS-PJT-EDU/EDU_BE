package com.education.takeit.chat.service;

import com.education.takeit.chat.entity.Chat;
import com.education.takeit.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;

    public void saveLog(String userMessage, String aiResponse, Long userId) {
        Chat chat =
                Chat.builder()
                        .userMessage(userMessage)
                        .aiMessage(aiResponse)
                        .userId(userId)
                        .build();
        chatRepository.save(chat);
    }
}

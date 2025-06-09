package com.education.takeit.chat.service;

import com.education.takeit.chat.dto.ChatFindResDto;
import com.education.takeit.chat.entity.Chat;
import com.education.takeit.chat.repository.ChatRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {
  private final ChatRepository chatRepository;

  public void saveLog(String userMessage, String aiResponse, Long userId) {
    Chat chat =
        Chat.builder().userMessage(userMessage).aiMessage(aiResponse).userId(userId).build();
    chatRepository.save(chat);
  }

  public List<ChatFindResDto> findUserChat(Long userId) {
    List<Chat> chats = chatRepository.findAllByUserId(userId);
    List<ChatFindResDto> chatFindResDtos = new ArrayList<>();

    for (Chat chat : chats) {
      ChatFindResDto chatFindResDto =
          new ChatFindResDto(chat.getUserMessage(), chat.getAiMessage(), chat.getChatTimestamp());
      chatFindResDtos.add(chatFindResDto);
    }

    return chatFindResDtos;
  }
}

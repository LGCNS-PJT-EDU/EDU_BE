package com.education.takeit.chat.controller;

import com.education.takeit.chat.dto.ChatReqDto;
import com.education.takeit.chat.dto.ChatResDto;
import com.education.takeit.chat.service.ChatService;
import com.education.takeit.global.client.AIClient;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final AIClient aiClient;
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat")         // ✔️ 클라이언트에서 /pub/chat 으로 보내야 매핑됨
    public ChatResDto handleChat(@Payload ChatReqDto request, Message<?> message) {

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            throw new IllegalStateException("헤더 접근 실패");
        }

        Long userId = (Long) accessor.getSessionAttributes().get("userId");

        ChatResDto response = aiClient.postChatMessage(request);
        chatService.saveLog(request.message(), response.message(), userId);

        messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/response",
                response);

        return response;
    }

}

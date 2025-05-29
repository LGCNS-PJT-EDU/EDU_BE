package com.education.takeit.chat.handler;

import com.education.takeit.global.security.JwtUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {
  private final JwtUtils jwtUtils;

  @Override
  public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
    StompHeaderAccessor accessor =
        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    if (accessor == null) {
      return message;
    }

    StompCommand command = accessor.getCommand();
    if (command == null) {
      return message;
    }

    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      try {
        String authHeader = accessor.getFirstNativeHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
          throw new IllegalArgumentException("No valid Authorization header found");
        }

        String token = authHeader.substring(7);
        Long userId = jwtUtils.getUserId(token);

        accessor.getSessionAttributes().put("userId", userId);

        // ⭐ convertAndSendToUser를 위한 Principal 설정
        accessor.setUser(new StompPrincipal(userId.toString()));

      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return message;
  }
}

package com.education.takeit.global.config;

import com.education.takeit.chat.handler.StompHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final StompHandler stompHandler;

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry
        .addEndpoint("/ws/chat") // 클라이언트 연결 URL
        .setAllowedOriginPatterns("*");
    //                .withSockJS();
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/queue"); // 구독 경로
    registry.setApplicationDestinationPrefixes("/pub"); // 클라이언트 송신 경로 접두사

    registry.setUserDestinationPrefix("/user");
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(stompHandler); // ChannelInterceptor 구현체
  }
}

package com.education.takeit.chat.controller;

import com.education.takeit.chat.dto.ChatFindResDto;
import com.education.takeit.chat.service.ChatService;
import com.education.takeit.global.dto.Message;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
@Tag(name = "챗봇", description = "챗봇 관련 API")
public class ChatbotController {

  private final ChatService chatService;

  @GetMapping
  @Operation(summary = "사용자의 채팅 기록 조회", description = "사용자의 전체 채팅과 응답을 반환하는 API")
  public ResponseEntity<Message<List<ChatFindResDto>>> getChat(
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    Long userId = userDetails.getUserId();
    List<ChatFindResDto> userChats = chatService.findUserChat(userId);
    return ResponseEntity.ok(new Message<>(StatusCode.OK, userChats));
  }
}

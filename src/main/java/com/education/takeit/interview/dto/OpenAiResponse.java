package com.education.takeit.interview.dto;

import com.education.takeit.global.dto.Message;
import java.util.List;

public record OpenAiResponse(List<Choice> choices) {
  public record Choice(Message message) {}

  public record Message(String role, String content) {}
}

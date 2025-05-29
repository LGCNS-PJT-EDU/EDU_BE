package com.education.takeit.global.client;

import com.education.takeit.interview.dto.OpenAiResponse;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Component
public class OpenAiRestClient {

  private final RestClient restClient;

  @Value("${openai.secret-key}")
  private String apiKey;

  public String requestInterviewFeedback(String prompt) {
    Map<String, Object> requestBody =
        Map.of(
            "model",
            "gpt-3.5-turbo",
            "messages",
            List.of(
                Map.of("role", "system", "content", "너는 면접 피드백을 제공하는 AI야."),
                Map.of("role", "user", "content", prompt)),
            "temperature",
            0.7 // 창의성 설정 정도
            );

    OpenAiResponse response =
        restClient
            .post()
            .uri("https://api.openai.com/v1/chat/completions")
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .body(requestBody)
            .retrieve()
            .body(OpenAiResponse.class);

    return response.choices().get(0).message().content();
  }
}

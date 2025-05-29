package com.education.takeit.global.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Component
public class OpenAiRestClient {
    private final RestClient restClient;
    @Value("${openai.api.key}")
    private String apiKey;
}

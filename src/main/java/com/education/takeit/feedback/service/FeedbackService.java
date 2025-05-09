package com.education.takeit.feedback.service;

import com.education.takeit.feedback.client.InternalClient;
import com.education.takeit.feedback.dto.FeedbackResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackService {

  private final InternalClient aiClient;

  public List<FeedbackResponseDto> findFeedback(String userId) {
    return aiClient.getFeedback(userId);
  }
}

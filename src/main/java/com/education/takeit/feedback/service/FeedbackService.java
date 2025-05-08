package com.education.takeit.feedback.service;

import com.education.takeit.feedback.client.InternalClient;
import com.education.takeit.feedback.dto.FeedbackResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final InternalClient aiClient;

    public List<FeedbackResponse> findFeedback(String userId) {
        return aiClient.getFeedback(userId);
    }
}

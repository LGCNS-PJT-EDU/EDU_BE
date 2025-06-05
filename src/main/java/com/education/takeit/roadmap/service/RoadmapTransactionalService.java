package com.education.takeit.roadmap.service;

import com.education.takeit.feedback.repository.FeedbackRepository;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.recommend.repository.UserContentRepository;
import com.education.takeit.roadmap.entity.Roadmap;
import com.education.takeit.roadmap.entity.RoadmapManagement;
import com.education.takeit.roadmap.repository.RoadmapManagementRepository;
import com.education.takeit.roadmap.repository.RoadmapRepository;
import com.education.takeit.solution.repository.UserExamAnswerRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoadmapTransactionalService {
  private final RoadmapManagementRepository roadmapManagementRepository;
  private final RoadmapRepository roadmapRepository;
  private final FeedbackRepository feedbackRepository;
  private final UserContentRepository userContentRepository;
  private final UserExamAnswerRepository userExamAnswerRepository;

  @Transactional
  public void deleteRoadmap(Long userId) {
    RoadmapManagement roadmapManagement = roadmapManagementRepository.findByUserId(userId);
    List<Roadmap> roadmaps =
        roadmapRepository.findByRoadmapManagement_RoadmapManagementId(
            roadmapManagement.getRoadmapManagementId());
    if (roadmaps.isEmpty()) {
      throw new CustomException(StatusCode.ROADMAP_NOT_FOUND);
    }
    roadmapRepository.deleteAll(roadmaps);
    roadmapManagementRepository.delete(roadmapManagement);
    userContentRepository.deleteByUser_UserId(userId);
    feedbackRepository.deleteByUser_UserId(userId);
    userExamAnswerRepository.deleteByUser_UserId(userId);
  }
}

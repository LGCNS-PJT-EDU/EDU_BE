package com.education.takeit.roadmap.service;

import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.roadmap.entity.Roadmap;
import com.education.takeit.roadmap.entity.RoadmapManagement;
import com.education.takeit.roadmap.repository.RoadmapManagementRepository;
import com.education.takeit.roadmap.repository.RoadmapRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoadmapTransactionalService {
    private final RoadmapManagementRepository roadmapManagementRepository;
    private final RoadmapRepository roadmapRepository;

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
    }
}

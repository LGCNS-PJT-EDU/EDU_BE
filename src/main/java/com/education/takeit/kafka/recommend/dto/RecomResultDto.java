package com.education.takeit.kafka.recommend.dto;

import com.education.takeit.recommend.dto.UserContentResDto;
import java.util.List;

public record RecomResultDto(Long userId, Long subjectId, List<UserContentResDto> recommendation) {}

package com.education.takeit.recommend.service;

import com.education.takeit.recommend.dto.UserContentResDto;
import com.education.takeit.recommend.entity.TotalContent;
import com.education.takeit.recommend.entity.UserContent;
import com.education.takeit.recommend.repository.UserContentRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecommendService {
  private final UserContentRepository userContentRepository;

  public List<UserContentResDto> getUserContent(long userId) {
    List<UserContent> userContents = userContentRepository.findByUserIdWithContent(userId);

    return userContents.stream()
        .map(
            uc -> {
              TotalContent tc = uc.getTotalContent();
              return new UserContentResDto(
                  tc.getContentTitle(),
                  tc.getContentUrl(),
                  tc.getContentType(),
                  tc.getContentPlatform(),
                  tc.getContentDuration().name(),
                  tc.getContentPrice().name());
            })
        .collect(Collectors.toList());
  }
}

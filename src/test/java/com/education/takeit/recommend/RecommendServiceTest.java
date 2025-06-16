package com.education.takeit.recommend;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.kafka.recommand.dto.RecomResultDto;
import com.education.takeit.recommend.dto.UserContentResDto;
import com.education.takeit.recommend.entity.TotalContent;
import com.education.takeit.recommend.entity.UserContent;
import com.education.takeit.recommend.repository.TotalContentRepository;
import com.education.takeit.recommend.repository.UserContentRepository;
import com.education.takeit.recommend.service.RecommendService;
import com.education.takeit.roadmap.entity.Subject;
import com.education.takeit.roadmap.entity.Track;
import com.education.takeit.roadmap.repository.SubjectRepository;
import com.education.takeit.user.entity.*;
import com.education.takeit.user.repository.UserRepository;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RecommendServiceTest {
  @InjectMocks private RecommendService recommendService;
  @Mock private UserContentRepository userContentRepository;
  @Mock private UserRepository userRepository;
  @Mock private SubjectRepository subjectRepository;
  @Mock private TotalContentRepository totalContentRepository;

  private Long userId;
  private Subject subject;
  private TotalContent content;
  private UserContent userContent;

  private TotalContent createTotalContent(Subject subject) {
    TotalContent content = new TotalContent();

    setField(content, "totalContentId", 1L);
    setField(content, "contentTitle", "title");
    setField(content, "contentUrl", "https://example.com/spring");
    setField(content, "contentType", "VIDEO");
    setField(content, "contentPlatform", "YOUTUBE");
    setField(content, "contentDuration", LectureAmount.HOUR_1);
    setField(content, "contentLevel", "초급");
    setField(content, "contentPrice", PriceLevel.FREE);
    setField(content, "subject", subject);

    return content;
  }

  private void setField(Object target, String fieldName, Object value) {
    try {
      Field field = target.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(target, value);
    } catch (Exception e) {
      throw new RuntimeException("필드 설정 실패: " + fieldName, e);
    }
  }

  @BeforeEach
  void setUp() {
    userId = 1L;
    subject =
        Subject.builder()
            .subId(1L)
            .subOverview("")
            .subNm("Java")
            .subType("BE")
            .subEssential("Y")
            .track(new Track())
            .baseSubOrder(1)
            .build();

    content = createTotalContent(subject);

    userContent =
        UserContent.builder()
            .totalContent(content)
            .isAiRecommended(true)
            .aiRecommendReason("AI 추천 사유")
            .build();
  }

  @Test
  @DisplayName("사용자 추천 컨텐츠 조회")
  void 사용자_추천_컨텐츠_조회_성공() {
    // given

    when(userContentRepository.findByUserIdWithContent(userId)).thenReturn(List.of(userContent));

    // when
    List<UserContentResDto> result = recommendService.getUserContent(userId);

    // then
    assertThat(result).hasSize(1);
    UserContentResDto dto = result.getFirst();
    assertThat(dto.contentId()).isEqualTo(1L);
    assertThat(dto.subjectId()).isEqualTo(1L);
    assertThat(dto.title()).isEqualTo("title");
    assertThat(dto.url()).isEqualTo("https://example.com/spring");
    assertThat(dto.type()).isEqualTo("VIDEO");
    assertThat(dto.platform()).isEqualTo("YOUTUBE");
    assertThat(dto.duaration()).isEqualTo("HOUR_1");
    assertThat(dto.price()).isEqualTo("FREE");
    assertThat(dto.isAiRecommendation()).isTrue();

    verify(userContentRepository).findByUserIdWithContent(userId);
  }

  @Test
  @DisplayName("사용자 과목별 추천 컨텐츠 조회 성공")
  void 사용자_과목별_추천_컨텐츠_조회_성공() {
    // given
    when(userContentRepository.findByUser_UserIdAndSubject_SubId(userId, subject.getSubId()))
        .thenReturn(List.of(userContent));

    // when
    List<UserContentResDto> result =
        recommendService.findRecommendations(userId, subject.getSubId());

    // then
    assertThat(result).hasSize(1);
    UserContentResDto dto = result.getFirst();
    assertThat(dto.contentId()).isEqualTo(1L);
    assertThat(dto.subjectId()).isEqualTo(1L);
    assertThat(dto.title()).isEqualTo("title");
    assertThat(dto.url()).isEqualTo("https://example.com/spring");
    assertThat(dto.type()).isEqualTo("VIDEO");
    assertThat(dto.platform()).isEqualTo("YOUTUBE");
    assertThat(dto.duaration()).isEqualTo("HOUR_1");
    assertThat(dto.price()).isEqualTo("FREE");
    assertThat(dto.isAiRecommendation()).isTrue();
  }

  @Test
  @DisplayName("사용자 추천 컨텐츠 저장 성공")
  void 사옹자_추천_컨텐츠_저장_성공() {
    // given
    User user = new User("test@test.com", "test", "password", LoginType.LOCAL, Role.USER);

    RecomResultDto recomResultDto =
        new RecomResultDto(
            userId,
            subject.getSubId(),
            List.of(
                new UserContentResDto(
                    content.getTotalContentId(),
                    subject.getSubId(),
                    content.getContentTitle(),
                    content.getContentUrl(),
                    content.getContentType(),
                    content.getContentPlatform(),
                    content.getContentDuration().name(),
                    content.getContentPrice().name(),
                    true,
                    "AI 추천 사유")));

    when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
    when(subjectRepository.findBySubId(subject.getSubId())).thenReturn(Optional.of(subject));
    when(totalContentRepository.findById(content.getTotalContentId()))
        .thenReturn(Optional.of(content));

    // when
    recommendService.saveUserContents(recomResultDto);

    // then
    verify(userRepository).findByUserId(userId);
    verify(subjectRepository).findBySubId(subject.getSubId());
    verify(totalContentRepository).findById(content.getTotalContentId());
    verify(userContentRepository).saveAll(anyList());
  }

  @Test
  @DisplayName("추천 콘텐츠 저장 시 컨텐츠가 존재하지 않으면 예외 발생")
  void 추천_콘텐츠_저장_실패_컨텐츠_없음() {
    // given
    User user = new User("test@test.com", "test", "password", LoginType.LOCAL, Role.USER);

    RecomResultDto dto =
        new RecomResultDto(
            userId,
            subject.getSubId(),
            List.of(
                new UserContentResDto(
                    content.getTotalContentId(), // 존재하지 않는 ID로 가정
                    subject.getSubId(),
                    content.getContentTitle(),
                    content.getContentUrl(),
                    content.getContentType(),
                    content.getContentPlatform(),
                    content.getContentDuration().name(),
                    content.getContentPrice().name(),
                    true,
                    "AI 추천 사유")));

    when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
    when(subjectRepository.findBySubId(subject.getSubId())).thenReturn(Optional.of(subject));
    when(totalContentRepository.findById(content.getTotalContentId())).thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> recommendService.saveUserContents(dto))
        .isInstanceOf(CustomException.class)
        .extracting("statusCode")
        .isEqualTo(StatusCode.CONTENTS_NOT_FOUND);

    verify(userRepository).findByUserId(userId);
    verify(subjectRepository).findBySubId(subject.getSubId());
    verify(totalContentRepository).findById(content.getTotalContentId());
  }
}

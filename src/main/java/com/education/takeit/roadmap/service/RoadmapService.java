package com.education.takeit.roadmap.service;

import com.education.takeit.roadmap.dto.RoadmapRequestDto;
import com.education.takeit.roadmap.dto.RoadmapResponseDto;
import com.education.takeit.roadmap.dto.SubjectDto;
import com.education.takeit.roadmap.entity.Roadmap;
import com.education.takeit.roadmap.entity.Subject;
import com.education.takeit.roadmap.repository.RoadmapRepository;
import com.education.takeit.roadmap.repository.SubjectRepository;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoadmapService {
  private final SubjectRepository subjectRepository;
  private final RoadmapRepository roadmapRepository;
  private final RedisTemplate<Object, Object> redisTemplate;

  public RoadmapResponseDto getRoadmap(List<RoadmapRequestDto> answers) {
    List<Subject> resultSubjects = new ArrayList<>();

    // BE, FE 정보 처리
    Optional<String> mainTrack =
        answers.stream()
            .filter(a -> a.questionId() == 1)
            .map(RoadmapRequestDto::answer)
            .findFirst();

    if (mainTrack.isEmpty()) {
      throw new IllegalArgumentException("BE, FE 분기 처리 오류");
    }

    String BEorFE = mainTrack.get(); // BE, FE 정보 저장

    // 필수 과목 추가
    List<Subject> essentialSubjects = subjectRepository.findBySubTypeAndSubEssential(BEorFE, "Y");
    resultSubjects.addAll(essentialSubjects);

    // 조건별 과목 처리
    int flag = 0;
    for (RoadmapRequestDto answer : answers) {
      if (answer.questionId() == 5) {
        if (answer.answer().equals("React")) {
          subjectRepository.findById(10L).ifPresent(resultSubjects::add);
          subjectRepository.findById(11L).ifPresent(resultSubjects::add);
          subjectRepository.findById(12L).ifPresent(resultSubjects::add);
          subjectRepository.findById(22L).ifPresent(resultSubjects::add);
          subjectRepository.findById(23L).ifPresent(resultSubjects::add);
          subjectRepository.findById(24L).ifPresent(resultSubjects::add);
        } else if (answer.answer().equals("Vue")) {
          subjectRepository.findById(13L).ifPresent(resultSubjects::add);
          subjectRepository.findById(14L).ifPresent(resultSubjects::add);
          subjectRepository.findById(25L).ifPresent(resultSubjects::add);
          subjectRepository.findById(26L).ifPresent(resultSubjects::add);
        } else if (answer.answer().equals("Angular")) {
          subjectRepository.findById(15L).ifPresent(resultSubjects::add);
          subjectRepository.findById(16L).ifPresent(resultSubjects::add);
          subjectRepository.findById(27L).ifPresent(resultSubjects::add);
          subjectRepository.findById(28L).ifPresent(resultSubjects::add);
        }
      }
      if (answer.questionId() == 6 && answer.answer().equals("Y")) {
        subjectRepository.findById(21L).ifPresent(resultSubjects::add);
      }
      if (answer.questionId() == 7 && answer.answer().equals("Y")) {
        subjectRepository.findById(17L).ifPresent(resultSubjects::add);
        subjectRepository.findById(18L).ifPresent(resultSubjects::add);
        subjectRepository.findById(19L).ifPresent(resultSubjects::add);
        subjectRepository.findById(20L).ifPresent(resultSubjects::add);
      }
      if (answer.questionId() == 8 && answer.answer().equals("Y")) {
        subjectRepository.findById(30L).ifPresent(resultSubjects::add);
        subjectRepository.findById(31L).ifPresent(resultSubjects::add);
        subjectRepository.findById(32L).ifPresent(resultSubjects::add);
      }
      if (answer.questionId() == 9 && answer.answer().equals("Y")) {
        subjectRepository.findById(33L).ifPresent(resultSubjects::add);
      }
      if (answer.questionId() == 10 && answer.answer().equals("Y")) {
        subjectRepository.findById(34L).ifPresent(resultSubjects::add);
      }
      if (answer.questionId() == 11) {
        if (answer.answer().equals("Java/Spring")) {
          subjectRepository.findById(39L).ifPresent(resultSubjects::add);
          subjectRepository.findById(46L).ifPresent(resultSubjects::add);
          flag = 1;
        } else if (answer.answer().equals("Python/Flask")) {
          subjectRepository.findById(40L).ifPresent(resultSubjects::add);
          subjectRepository.findById(49L).ifPresent(resultSubjects::add);
          flag = 2;
        } else if (answer.answer().equals("Python/Django")) {
          subjectRepository.findById(40L).ifPresent(resultSubjects::add);
          subjectRepository.findById(48L).ifPresent(resultSubjects::add);
          flag = 3;
        } else if (answer.answer().equals("Js/Node")) {
          subjectRepository.findById(41L).ifPresent(resultSubjects::add);
          subjectRepository.findById(47L).ifPresent(resultSubjects::add);
          flag = 4;
        } else if (answer.answer().equals("Kotlin/Spring")) {
          subjectRepository.findById(42L).ifPresent(resultSubjects::add);
          subjectRepository.findById(50L).ifPresent(resultSubjects::add);
          flag = 5;
        }
      }
      if (answer.questionId() == 12 && answer.answer().equals("Y")) {
        subjectRepository.findById(44L).ifPresent(resultSubjects::add);
      }
      if (answer.questionId() == 13 && answer.answer().equals("Y")) {
        subjectRepository.findById(45L).ifPresent(resultSubjects::add);
      }
      if (answer.questionId() == 14 && answer.answer().equals("Y")) {
        if (flag == 1) {
          subjectRepository.findById(51L).ifPresent(resultSubjects::add);
        } else if (flag == 2) {
          subjectRepository.findById(57L).ifPresent(resultSubjects::add);
        } else if (flag == 3) {
          subjectRepository.findById(55L).ifPresent(resultSubjects::add);
        } else if (flag == 4) {
          subjectRepository.findById(53L).ifPresent(resultSubjects::add);
        } else if (flag == 5) {
          subjectRepository.findById(51L).ifPresent(resultSubjects::add);
        }
      }
      if (answer.questionId() == 15 && answer.answer().equals("Y")) {
        if (flag == 1) {
          subjectRepository.findById(52L).ifPresent(resultSubjects::add);
        } else if (flag == 2) {
          subjectRepository.findById(58L).ifPresent(resultSubjects::add);
        } else if (flag == 3) {
          subjectRepository.findById(56L).ifPresent(resultSubjects::add);
        } else if (flag == 4) {
          subjectRepository.findById(54L).ifPresent(resultSubjects::add);
        } else if (flag == 5) {
          subjectRepository.findById(52L).ifPresent(resultSubjects::add);
        }
      }
    }

    // 중복 제거 및 정렬
    List<SubjectDto> subjects =
        resultSubjects.stream()
            .distinct()
            .sorted(Comparator.comparingInt(Subject::getBaseSubOrder))
            .map(s -> new SubjectDto(s.getSubId(), s.getSubNm(), s.getBaseSubOrder()))
            .collect(Collectors.toList());

    // 5. UUID 생성 및 Redis 저장
    String uuid = UUID.randomUUID().toString();
    String key = "roadmap:" + uuid;

    // JSON 직렬화 또는 간단한 리스트 저장 예시 (여기선 ID 리스트 저장)
    String subjectIds =
        resultSubjects.stream().map(s -> s.getSubId().toString()).collect(Collectors.joining(","));

    redisTemplate.opsForValue().set(key, subjectIds, Duration.ofMinutes(30));
    System.out.println("Redis 저장 키: " + key);

    return new RoadmapResponseDto(uuid, subjects);
  }

  public int getProgressPercentage(Long userId) {
    List<Roadmap> roadmaps = roadmapRepository.findByUserId(userId);
    if (roadmaps.isEmpty()) return 0;

    long total = roadmaps.size();
    long completed = roadmaps.stream().filter(Roadmap::isComplete).count();

    return (int) ((double) completed / total * 100);
  }
}

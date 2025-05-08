package com.education.takeit.roadmap.service;

import com.education.takeit.diagnosis.dto.DiagnosisAnswerRequest;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.global.security.JwtUtils;
import com.education.takeit.roadmap.dto.RoadmapResponseDto;
import com.education.takeit.roadmap.dto.SubjectDto;
import com.education.takeit.roadmap.entity.Roadmap;
import com.education.takeit.roadmap.entity.RoadmapManagement;
import com.education.takeit.roadmap.entity.Subject;
import com.education.takeit.roadmap.repository.RoadmapManagementRepository;
import com.education.takeit.roadmap.repository.RoadmapRepository;
import com.education.takeit.roadmap.repository.SubjectRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoadmapService {
  private final ObjectMapper objectMapper;
  private final SubjectRepository subjectRepository;
  private final RedisTemplate<String, String> redisTemplate;
  private final long roadmapTime = 1000L * 60 * 30;
  private final RoadmapManagementRepository roadmapManagementRepository;

  private final JwtUtils jwtUtils;
  private final RoadmapRepository roadmapRepository;

  public RoadmapResponseDto roadmapSelect(String flag, List<DiagnosisAnswerRequest> answers) {

    RoadmapResponseDto roadmapResponseDto = createRoadmap(answers);

    if (flag == null) {
      // uuid 생성
      String guestUuid = UUID.randomUUID().toString();

      // SubjecIds 저장
      String subjectIds =
          roadmapResponseDto.subjects().stream()
              .map(subject -> subject.subjectId().toString())
              .collect(Collectors.joining(","));
      // 공통질문 2~4 응답 레디스에 저장
      try {
        String answersJson = objectMapper.writeValueAsString(answers); // answers → JSON으로 변환

        redisTemplate
            .opsForValue()
            .set("guest:" + guestUuid + ":subjects", subjectIds, Duration.ofMinutes(15));
        redisTemplate
            .opsForValue()
            .set("guest:" + guestUuid + ":answers", answersJson, Duration.ofMinutes(15));

      } catch (JsonProcessingException e) {
        throw new RuntimeException("answers 직렬화 실패", e);
      }

      System.out.println("guest roadmap create: " + guestUuid);
      return new RoadmapResponseDto(guestUuid, roadmapResponseDto.subjects());

    } else {
      // 개인 roadmap 데이터 저장
      List<Long> subjectIds =
          roadmapResponseDto.subjects().stream()
              .map(SubjectDto::subjectId)
              .collect(Collectors.toList());

      saveRoadmap(flag, subjectIds, answers);

      System.out.println("user roadmap create:" + flag);

      return roadmapResponseDto;
    }
  }

  public RoadmapResponseDto createRoadmap(List<DiagnosisAnswerRequest> answers) {

    // BE, FE 분기
    Optional<String> mainTrack =
        answers.stream()
            .filter(a -> a.questionId() == 1)
            .map(DiagnosisAnswerRequest::answer)
            .findFirst();

    if (mainTrack.isEmpty()) {
      throw new IllegalArgumentException("BE, FE 분기 처리 오류");
    }

    String BEorFE = mainTrack.get(); // BE, FE 정보 저장

    // 필수 과목 추가
    List<Subject> essentialSubjects = subjectRepository.findBySubTypeAndSubEssential(BEorFE, "Y");
    List<Subject> resultSubjects = new ArrayList<>(essentialSubjects);

    // 조건별 과목 처리
    int flag = 0;
    for (DiagnosisAnswerRequest answer : answers) {
      if (answer.questionId() == 5) {
        switch (answer.answer()) {
          case "React" -> {
            subjectRepository.findById(10L).ifPresent(resultSubjects::add);
            subjectRepository.findById(11L).ifPresent(resultSubjects::add);
            subjectRepository.findById(12L).ifPresent(resultSubjects::add);
            subjectRepository.findById(22L).ifPresent(resultSubjects::add);
            subjectRepository.findById(23L).ifPresent(resultSubjects::add);
            subjectRepository.findById(24L).ifPresent(resultSubjects::add);
          }
          case "Vue" -> {
            subjectRepository.findById(13L).ifPresent(resultSubjects::add);
            subjectRepository.findById(14L).ifPresent(resultSubjects::add);
            subjectRepository.findById(25L).ifPresent(resultSubjects::add);
            subjectRepository.findById(26L).ifPresent(resultSubjects::add);
          }
          case "Angular" -> {
            subjectRepository.findById(15L).ifPresent(resultSubjects::add);
            subjectRepository.findById(16L).ifPresent(resultSubjects::add);
            subjectRepository.findById(27L).ifPresent(resultSubjects::add);
            subjectRepository.findById(28L).ifPresent(resultSubjects::add);
          }
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
        switch (answer.answer()) {
          case "Java/Spring" -> {
            subjectRepository.findById(39L).ifPresent(resultSubjects::add);
            subjectRepository.findById(46L).ifPresent(resultSubjects::add);
            flag = 1;
          }
          case "Python/Flask" -> {
            subjectRepository.findById(40L).ifPresent(resultSubjects::add);
            subjectRepository.findById(49L).ifPresent(resultSubjects::add);
            flag = 2;
          }
          case "Python/Django" -> {
            subjectRepository.findById(40L).ifPresent(resultSubjects::add);
            subjectRepository.findById(48L).ifPresent(resultSubjects::add);
            flag = 3;
          }
          case "Js/Node" -> {
            subjectRepository.findById(41L).ifPresent(resultSubjects::add);
            subjectRepository.findById(47L).ifPresent(resultSubjects::add);
            flag = 4;
          }
          case "Kotlin/Spring" -> {
            subjectRepository.findById(42L).ifPresent(resultSubjects::add);
            subjectRepository.findById(50L).ifPresent(resultSubjects::add);
            flag = 5;
          }
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

    return new RoadmapResponseDto("??", subjects);
  }

  public void saveRoadmap(
      String flag, List<Long> subjectIds, List<DiagnosisAnswerRequest> answers) {
    Long userId = jwtUtils.getUserId(flag);

    Integer lectureAmount = null;
    Integer priceLevel = null;
    Boolean likesBooks = null;

    for (DiagnosisAnswerRequest answer : answers) {
      long questionId = answer.questionId();

      if (questionId == 2) {
        lectureAmount = Integer.parseInt(answer.answer());
      } else if (questionId == 3) {
        priceLevel = Integer.parseInt(answer.answer());
      } else if (questionId == 4) {
        likesBooks = answer.answer().equals("Y"); // Y: true, N: false
      }
    }

    RoadmapManagement roadmapManagement =
        RoadmapManagement.builder()
            .roadmapNm("Roadmap")
            .roadmapTimestamp(LocalDateTime.now())
            .lectureAmount(lectureAmount)
            .priceLevel(priceLevel)
            .likesBooks(likesBooks)
            .build();

    roadmapManagementRepository.save(roadmapManagement);

    int order = 1;
    for (Long subjectId : subjectIds) {
      Subject subject =
          subjectRepository
              .findById(subjectId)
              .orElseThrow(() -> new RuntimeException("Subject " + subjectId + " not found"));

      Roadmap roadmap =
          Roadmap.builder()
              .userId(userId)
              .roadmapManagement(roadmapManagement)
              .subject(subject)
              .orderSub(order++)
              .build();

      roadmapRepository.save(roadmap);
    }
  }

  public void saveGuestRoadmap(String uuid, String jwt) {
    String redisSubjects = redisTemplate.opsForValue().get("guest:" + uuid + ":subjects");
    String redisAnswersJson = redisTemplate.opsForValue().get("guest:" + uuid + ":answers");

    if (redisSubjects == null || redisAnswersJson == null) {
      throw new IllegalArgumentException("해당 게스트의 로드맵 데이터가 존재하지 않습니다 Guest UUID: " + uuid);
    }

    // subjectIds 문자열 → List<Long> 로 변환
    List<Long> subjectIds = Arrays.stream(redisSubjects.split(",")).map(Long::parseLong).toList();

    // JSON을 List<DiagnosisAnswerRequest>로 바꿔주기
    List<DiagnosisAnswerRequest> answers;
    try {
      answers =
          objectMapper.readValue(
              redisAnswersJson, new TypeReference<List<DiagnosisAnswerRequest>>() {});
    } catch (JsonProcessingException e) {
      throw new RuntimeException("answers 역직렬화 실패", e);
    }

    saveRoadmap(jwt, subjectIds, answers);

    redisTemplate.delete("guest:" + uuid + ":subjects");
    redisTemplate.delete("guest:" + uuid + ":answers");
  }

  public int getProgressPercentage(Long userId) {
    List<Roadmap> roadmaps = roadmapRepository.findByUserId(userId);
    if (roadmaps.isEmpty()) return 0;

    long total = roadmaps.size();
    long completed = roadmaps.stream().filter(Roadmap::isComplete).count();

    return (int) ((double) completed / total * 100);
  }

  public void updateRoadmap(Long userId, List<SubjectDto> subjects) {
    List<Roadmap> existingRoadmaps = roadmapRepository.findAllByUserId(userId);

    if (existingRoadmaps.isEmpty()) {
      throw new CustomException(StatusCode.ROADMAP_NOT_FOUND);
    }
    RoadmapManagement roadmapManagement = existingRoadmaps.get(0).getRoadmapManagement();
    roadmapManagement.setRoadmapTimestamp(LocalDateTime.now());
    roadmapManagementRepository.save(roadmapManagement);

    Map<Long, Roadmap> existingMap =
        existingRoadmaps.stream()
            .collect(
                Collectors.toMap(
                    roadmap -> roadmap.getSubject().getSubId(),
                    roadmap -> roadmap,
                    (existing, replacement) -> replacement));
    List<Roadmap> toSave = new ArrayList<>();
    Set<Long> updatedSubjectIds = new HashSet<>();

    for (SubjectDto dto : subjects) {
      Long subjectId = dto.subjectId();
      int order = dto.subjectOrder();
      updatedSubjectIds.add(subjectId);

      Roadmap roadmap = existingMap.get(subjectId);
      if (roadmap != null) { // 이미 있는 과목이면
        roadmap.setOrderSub(order); // 순서만 업데이트
      } else { // 새로 추가할 과목이면 생성
        Subject subject =
            subjectRepository
                .findById(subjectId)
                .orElseThrow(() -> new EntityNotFoundException("해당 과목 없음:" + subjectId));
        roadmap =
            Roadmap.builder()
                .userId(userId)
                .subject(subject)
                .orderSub(order)
                .roadmapManagement(roadmapManagement)
                .isComplete(false)
                .build();
      }
      toSave.add(roadmap);
    }
    // 기존에는 있었는데 수정 사항에 없는 과목은 삭제
    List<Roadmap> toDelete =
        existingRoadmaps.stream()
            .filter(roadmap -> !updatedSubjectIds.contains(roadmap.getSubject().getSubId()))
            .collect(Collectors.toList());
    roadmapRepository.deleteAll(toDelete);
    roadmapRepository.saveAll(toSave);
  }

  @Transactional
  public void deleteRoadmap(Long userId) {
    List<Roadmap> roadmaps = roadmapRepository.findAllByUserId(userId);
    if (roadmaps.isEmpty()) {
      throw new CustomException(StatusCode.ROADMAP_NOT_FOUND);
    }
    RoadmapManagement roadmapManagement = roadmaps.get(0).getRoadmapManagement();
    roadmapRepository.deleteAll(roadmaps);
    roadmapManagementRepository.delete(roadmapManagement);
  }
}

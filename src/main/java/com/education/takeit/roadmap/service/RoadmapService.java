package com.education.takeit.roadmap.service;

import com.education.takeit.diagnosis.dto.DiagnosisAnswerRequest;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.roadmap.dto.MyPageResDto;
import com.education.takeit.roadmap.dto.RoadmapFindResDto;
import com.education.takeit.roadmap.dto.RoadmapSaveResDto;
import com.education.takeit.roadmap.dto.SubjectDto;
import com.education.takeit.roadmap.entity.*;
import com.education.takeit.roadmap.repository.RoadmapManagementRepository;
import com.education.takeit.roadmap.repository.RoadmapRepository;
import com.education.takeit.roadmap.repository.SubjectRepository;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
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
  private final RoadmapManagementRepository roadmapManagementRepository;
  private final RoadmapRepository roadmapRepository;
  private final UserRepository userRepository;
  private final RoadmapTransactionalService roadmapTransactionalService;

  public RoadmapSaveResDto selectRoadmap(Long userId, List<DiagnosisAnswerRequest> answers) {

    RoadmapSaveResDto roadmapSaveResDto = createRoadmap(answers);

    if (userId == null) {
      // uuid 생성
      String guestUuid = UUID.randomUUID().toString();

      // SubjectIds 저장
      String subjectIds =
          roadmapSaveResDto.subjects().stream()
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
      return new RoadmapSaveResDto(
          guestUuid, roadmapSaveResDto.userLocationSubjectId(), roadmapSaveResDto.subjects());

    } else {
      RoadmapManagement roadmapManagement = roadmapManagementRepository.findByUserId(userId);
      if (roadmapManagement != null) {
        throw new CustomException(StatusCode.ALREADY_EXIST_ROADMAP);
      }

      // 개인 roadmap 데이터 저장
      List<Long> subjectIds =
          roadmapSaveResDto.subjects().stream()
              .map(SubjectDto::subjectId)
              .collect(Collectors.toList());

      saveRoadmap(userId, subjectIds, answers);

      return roadmapSaveResDto;
    }
  }

  public RoadmapSaveResDto createRoadmap(List<DiagnosisAnswerRequest> answers) {

    // BE, FE 분기
    Optional<String> mainTrack =
        answers.stream()
            .filter(a -> a.questionId() == 1)
            .map(DiagnosisAnswerRequest::answer)
            .findFirst();

    if (mainTrack.isEmpty()) {
      throw new CustomException(StatusCode.ROADMAP_TYPE_NOT_FOUND);
    }

    String BEorFE = mainTrack.get(); // BE, FE 정보 저장

    // 필수 과목 추가
    List<Subject> essentialSubjects = subjectRepository.findBySubTypeAndSubEssential(BEorFE, "Y");
    List<Subject> resultSubjects = new ArrayList<>(essentialSubjects);

    long defaultLocationSubjectId = 0L;
    if (BEorFE.equals("FE")) {
      defaultLocationSubjectId = 1L;
    } else if (BEorFE.equals("BE")) {
      defaultLocationSubjectId = 35L;
    }

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
          subjectRepository.findById(58L).ifPresent(resultSubjects::add);
        } else if (flag == 3) {
          subjectRepository.findById(56L).ifPresent(resultSubjects::add);
        } else if (flag == 4) {
          subjectRepository.findById(54L).ifPresent(resultSubjects::add);
        } else if (flag == 5) {
          subjectRepository.findById(52L).ifPresent(resultSubjects::add);
        }
      }
      if (answer.questionId() == 15 && answer.answer().equals("Y")) {
        if (flag == 1) {
          subjectRepository.findById(53L).ifPresent(resultSubjects::add);
        } else if (flag == 2) {
          subjectRepository.findById(59L).ifPresent(resultSubjects::add);
        } else if (flag == 3) {
          subjectRepository.findById(57L).ifPresent(resultSubjects::add);
        } else if (flag == 4) {
          subjectRepository.findById(55L).ifPresent(resultSubjects::add);
        } else if (flag == 5) {
          subjectRepository.findById(53L).ifPresent(resultSubjects::add);
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

    return new RoadmapSaveResDto("사용자는 uuid가 없어요", defaultLocationSubjectId, subjects);
  }

  public void saveRoadmap(
      Long userId, List<Long> subjectIds, List<DiagnosisAnswerRequest> answers) {

    if (roadmapManagementRepository.findByUserId(userId) != null) {
      throw new CustomException(StatusCode.ALREADY_EXIST_ROADMAP);
    }

    LectureAmount lectureAmount = null;
    PriceLevel priceLevel = null;
    Boolean likesBooks = null;

    for (DiagnosisAnswerRequest answer : answers) {
      long questionId = answer.questionId();
      String value = answer.answer();

      if (questionId == 2) {
        int idx = Integer.parseInt(value);
        lectureAmount = LectureAmount.values()[idx];
      } else if (questionId == 3) {
        int idx = Integer.parseInt(value);
        priceLevel = PriceLevel.values()[idx];
      } else if (questionId == 4) {
        likesBooks = value.equals("Y");
      }
    }

    RoadmapManagement roadmapManagement =
        RoadmapManagement.builder()
            .roadmapNm("Roadmap")
            .roadmapTimestamp(LocalDateTime.now())
            .userId(userId)
            .lectureAmount(lectureAmount)
            .priceLevel(priceLevel)
            .likesBooks(likesBooks)
            .build();

    roadmapManagementRepository.save(roadmapManagement);

    List<Subject> subjectList = subjectRepository.findAllById(subjectIds);

    // ID → Subject 매핑
    Map<Long, Subject> subjectMap =
        subjectList.stream().collect(Collectors.toMap(Subject::getSubId, s -> s));

    List<Roadmap> roadmapList = new ArrayList<>();
    int order = 1;

    for (Long subjectId : subjectIds) {
      Subject subject = subjectMap.get(subjectId);
      if (subject == null) {
        throw new CustomException(StatusCode.SUBJECT_NOT_FOUND);
      }

      Roadmap roadmap =
          Roadmap.builder()
              .roadmapManagement(roadmapManagement)
              .subject(subject)
              .orderSub(order++)
              .isComplete(false)
              .preSubmitCount(0)
              .postSubmitCount(0)
              .build();

      roadmapList.add(roadmap);
    }
    roadmapRepository.saveAll(roadmapList);
  }

  public RoadmapSaveResDto saveGuestRoadmap(String uuid, Long userId) {
    String redisSubjects = redisTemplate.opsForValue().get("guest:" + uuid + ":subjects");
    String redisAnswersJson = redisTemplate.opsForValue().get("guest:" + uuid + ":answers");

    if (redisSubjects == null || redisAnswersJson == null) {
      throw new CustomException(StatusCode.ROADMAP_NOT_FOUND);
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

    // 로드맵 반환을 위한 subject List 생성
    List<Subject> subjects = subjectRepository.findAllById(subjectIds);

    List<SubjectDto> subjectDtos =
        subjects.stream()
            .map(
                subject ->
                    new SubjectDto(
                        subject.getSubId(), subject.getSubNm(), subject.getBaseSubOrder()))
            .toList();

    Long userLocationSubjectId = subjectDtos.isEmpty() ? null : subjectDtos.getFirst().subjectId();

    saveRoadmap(userId, subjectIds, answers);

    redisTemplate.delete("guest:" + uuid + ":subjects");
    redisTemplate.delete("guest:" + uuid + ":answers");

    return new RoadmapSaveResDto("uuid로 로드맵 생성 완료", userLocationSubjectId, subjectDtos);
  }

  public MyPageResDto getProgressPercentage(Long userId) {
    User user =
        userRepository
            .findByUserId(userId)
            .orElseThrow(() -> new CustomException(StatusCode.NOT_EXIST_USER));

    RoadmapManagement roadmapManagement = roadmapManagementRepository.findByUserId(userId);
    if (roadmapManagement == null) return new MyPageResDto(user.getNickname(), 0);

    List<Roadmap> roadmaps =
        roadmapRepository.findByRoadmapManagement_RoadmapManagementId(
            roadmapManagement.getRoadmapManagementId());

    long total = roadmaps.size();
    long completed = roadmaps.stream().filter(Roadmap::isComplete).count();

    int percent = (int) ((double) completed / total * 100);

    return new MyPageResDto(user.getNickname(), percent);
  }

  public void updateRoadmap(Long userId, List<SubjectDto> subjects) {

    RoadmapManagement roadmapManagement = roadmapManagementRepository.findByUserId(userId);
    List<Roadmap> existingRoadmaps =
        roadmapRepository.findByRoadmapManagement_RoadmapManagementId(
            roadmapManagement.getRoadmapManagementId());

    if (existingRoadmaps.isEmpty()) {
      throw new CustomException(StatusCode.ROADMAP_NOT_FOUND);
    }

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
                .subject(subject)
                .orderSub(order)
                .roadmapManagement(roadmapManagement)
                .isComplete(false)
                .preSubmitCount(0)
                .postSubmitCount(0)
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

  public List<SubjectDto> getDefaultRoadmap(String defaultRoadmapType) {
    long roadmapId;

    if (defaultRoadmapType.equals("FE")) {
      roadmapId = 1L;
    } else if (defaultRoadmapType.equals("BE")) {
      roadmapId = 2L;
    } else {
      throw new CustomException(StatusCode.ROADMAP_TYPE_NOT_FOUND);
    }

    List<Roadmap> roadmaps =
        roadmapRepository.findByRoadmapManagement_RoadmapManagementId(roadmapId);

    return roadmaps.stream()
        .sorted(Comparator.comparing(Roadmap::getOrderSub))
        .map(
            r -> {
              Subject s = r.getSubject();
              return new SubjectDto(s.getSubId(), s.getSubNm(), s.getBaseSubOrder());
            })
        .collect(Collectors.toList());
  }

  public RoadmapSaveResDto saveDefaultRoadmap(String roadmapType, Long userId) {

    if (roadmapManagementRepository.findByUserId(userId) != null) {
      roadmapTransactionalService.deleteRoadmap(userId);
    }

    long roadmapManagementId = 0L;
    long userLocationSubjectId = 0L;

    if (roadmapType.equals("FE")) {
      roadmapManagementId = 1L;
      userLocationSubjectId = 1L;
    } else if (roadmapType.equals("BE")) {
      roadmapManagementId = 2L;
      userLocationSubjectId = 35L;
    } else {
      throw new CustomException(StatusCode.ROADMAP_TYPE_NOT_FOUND);
    }

    List<Roadmap> defaultRoadmapList =
        roadmapRepository.findByRoadmapManagement_RoadmapManagementId(roadmapManagementId);

    if (defaultRoadmapList.isEmpty()) {
      throw new CustomException(StatusCode.DEFAULT_ROADMAP_NOT_FOUND);
    }

    // 새로운 로드맵 관리 정보 생성
    RoadmapManagement roadmapManagement =
        RoadmapManagement.builder()
            .roadmapNm("Default " + roadmapType + " Roadmap")
            .roadmapTimestamp(LocalDateTime.now())
            .userId(userId)
            .build();

    roadmapManagementRepository.save(roadmapManagement);

    int order = 1;
    for (Roadmap defaultRoadmap : defaultRoadmapList) {
      Roadmap newRoadmap =
          Roadmap.builder()
              .roadmapManagement(roadmapManagement)
              .subject(defaultRoadmap.getSubject())
              .orderSub(order++)
              .isComplete(false)
              .postSubmitCount(0)
              .preSubmitCount(0)
              .build();

      roadmapRepository.save(newRoadmap);
    }

    return new RoadmapSaveResDto(
        "user Default Roadmap", userLocationSubjectId, getDefaultRoadmap(roadmapType));
  }

  public RoadmapFindResDto findUserRoadmap(Long userId) {

    RoadmapManagement userRoadmapManagement = roadmapManagementRepository.findByUserId(userId);
    if(userRoadmapManagement == null) {
      throw new CustomException(StatusCode.ROADMAP_NOT_FOUND);
    }

    List<Roadmap> userRoadmaps =
        roadmapRepository.findByRoadmapManagement_RoadmapManagementId(
            userRoadmapManagement.getRoadmapManagementId());

    if (userRoadmaps.isEmpty()) {
      throw new CustomException(StatusCode.ROADMAP_NOT_FOUND);
    }

    Long userLocationSubjectId = findUserLocationRoadmap(userRoadmaps);

    List<SubjectDto> subjects =
        userRoadmaps.stream()
            .sorted(Comparator.comparing(Roadmap::getOrderSub))
            .map(
                r ->
                    new SubjectDto(
                        r.getSubject().getSubId(), r.getSubject().getSubNm(), r.getOrderSub()))
            .toList();

    String roadmapName = userRoadmaps.getFirst().getRoadmapManagement().getRoadmapNm();

    return new RoadmapFindResDto(subjects, roadmapName, userLocationSubjectId);
  }

  public Long findUserLocationRoadmap(List<Roadmap> roadmaps) {
    return roadmaps.stream()
        .sorted(Comparator.comparing(Roadmap::getOrderSub))
        .filter(r -> !r.isComplete())
        .map(r -> r.getSubject().getSubId())
        .findFirst()
        .orElse(null);
  }

  public RoadmapSaveResDto saveNewRoadmap(Long userId, List<DiagnosisAnswerRequest> answers) {
    roadmapTransactionalService.deleteRoadmap(userId);

    RoadmapSaveResDto roadmapSaveResDto = createRoadmap(answers);

    List<Long> subjectIds =
        roadmapSaveResDto.subjects().stream()
            .map(SubjectDto::subjectId)
            .collect(Collectors.toList());

    saveRoadmap(userId, subjectIds, answers);

    return roadmapSaveResDto;
  }
}

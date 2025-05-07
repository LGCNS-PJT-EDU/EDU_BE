package com.education.takeit.roadmap.service;

import com.education.takeit.global.security.JwtUtils;
import com.education.takeit.roadmap.dto.RoadmapRequestDto;
import com.education.takeit.roadmap.dto.RoadmapResponseDto;
import com.education.takeit.roadmap.dto.SubjectDto;
import com.education.takeit.roadmap.entity.Roadmap;
import com.education.takeit.roadmap.entity.RoadmapManagement;
import com.education.takeit.roadmap.entity.Subject;
import com.education.takeit.roadmap.repository.RoadmapManagementRepository;
import com.education.takeit.roadmap.repository.RoadmapRepository;
import com.education.takeit.roadmap.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoadmapService {
    private final SubjectRepository subjectRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final long roadmapTime = 1000L * 60 * 30;
    private final RoadmapManagementRepository roadmapManagementRepository;

    private final JwtUtils jwtUtils;
    private final RoadmapRepository roadmapRepository;


    public RoadmapResponseDto roadmapSelect(String flag, List<RoadmapRequestDto> answers){

        RoadmapResponseDto roadmapResponseDto = createRoadmap(answers);

        if(flag == null){
            //uuid 생성
            String guestUuid = UUID.randomUUID().toString();

            String subjectIds = roadmapResponseDto.subjects().stream()
                    .map(subject -> subject.subjectId().toString())
                    .collect(Collectors.joining(","));

            redisTemplate.opsForValue().set(guestUuid, subjectIds, Duration.ofMinutes(15));

            System.out.println("guest roadmap create:" + guestUuid);

            return new RoadmapResponseDto(guestUuid, roadmapResponseDto.subjects());
        }
        else{
            //개인 roadmap 데이터 저장
            List<Long> subjectIds = roadmapResponseDto.subjects().stream()
                            .map(SubjectDto::subjectId)
                                    .collect(Collectors.toList());

            saveRoadmap(flag, subjectIds);

            System.out.println("user roadmap create:" + flag);

            return roadmapResponseDto;
        }
    }

    public RoadmapResponseDto createRoadmap(List<RoadmapRequestDto> answers) {

        // BE, FE 분기
        Optional<String> mainTrack = answers.stream()
                .filter(a -> a.questionId() == 1)
                .map(RoadmapRequestDto::answer)
                .findFirst();

        if (mainTrack.isEmpty()) {
            throw new IllegalArgumentException("BE, FE 분기 처리 오류");
        }

        String BEorFE = mainTrack.get();  // BE, FE 정보 저장

        // 필수 과목 추가
        List<Subject> essentialSubjects =   subjectRepository.findBySubTypeAndSubEssential(BEorFE, "Y");
        List<Subject> resultSubjects = new ArrayList<>(essentialSubjects);

        // 조건별 과목 처리
        int flag = 0;
        for(RoadmapRequestDto answer: answers) {
            if(answer.questionId() == 5) {
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
            if(answer.questionId() == 6 && answer.answer().equals("Y")) {
                subjectRepository.findById(21L).ifPresent(resultSubjects::add);
            }
            if(answer.questionId() == 7 && answer.answer().equals("Y")) {
                subjectRepository.findById(17L).ifPresent(resultSubjects::add);
                subjectRepository.findById(18L).ifPresent(resultSubjects::add);
                subjectRepository.findById(19L).ifPresent(resultSubjects::add);
                subjectRepository.findById(20L).ifPresent(resultSubjects::add);
            }
            if(answer.questionId() == 8 && answer.answer().equals("Y")) {
                subjectRepository.findById(30L).ifPresent(resultSubjects::add);
                subjectRepository.findById(31L).ifPresent(resultSubjects::add);
                subjectRepository.findById(32L).ifPresent(resultSubjects::add);
            }
            if(answer.questionId() == 9 && answer.answer().equals("Y")) {
                subjectRepository.findById(33L).ifPresent(resultSubjects::add);
            }
            if(answer.questionId() == 10 && answer.answer().equals("Y")) {
                subjectRepository.findById(34L).ifPresent(resultSubjects::add);
            }
            if(answer.questionId() == 11) {
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
            if(answer.questionId() == 12 && answer.answer().equals("Y")) {
                subjectRepository.findById(44L).ifPresent(resultSubjects::add);
            }
            if (answer.questionId() == 13 && answer.answer().equals("Y")) {
                subjectRepository.findById(45L).ifPresent(resultSubjects::add);
            }
            if (answer.questionId() == 14 && answer.answer().equals("Y")) {
                if(flag == 1){
                    subjectRepository.findById(51L).ifPresent(resultSubjects::add);
                }
                else if(flag == 2){
                    subjectRepository.findById(57L).ifPresent(resultSubjects::add);
                }
                else if(flag == 3){
                    subjectRepository.findById(55L).ifPresent(resultSubjects::add);
                }
                else if(flag == 4){
                    subjectRepository.findById(53L).ifPresent(resultSubjects::add);
                }
                else if(flag == 5){
                    subjectRepository.findById(51L).ifPresent(resultSubjects::add);
                }
            }
            if (answer.questionId() == 15 && answer.answer().equals("Y")) {
                if(flag == 1){
                    subjectRepository.findById(52L).ifPresent(resultSubjects::add);
                }
                else if(flag == 2){
                    subjectRepository.findById(58L).ifPresent(resultSubjects::add);
                }
                else if(flag == 3){
                    subjectRepository.findById(56L).ifPresent(resultSubjects::add);
                }
                else if(flag == 4){
                    subjectRepository.findById(54L).ifPresent(resultSubjects::add);
                }
                else if(flag == 5){
                    subjectRepository.findById(52L).ifPresent(resultSubjects::add);
                }
            }
        }

        // 중복 제거 및 정렬
        List<SubjectDto> subjects = resultSubjects.stream()
                .distinct()
                .sorted(Comparator.comparingInt(Subject::getBaseSubOrder))
                .map(s -> new SubjectDto(s.getSubId(), s.getSubNm(), s.getBaseSubOrder()))
                .collect(Collectors.toList());

        return new RoadmapResponseDto("??", subjects);
    }

    public void saveRoadmap(String flag, List<Long> subjectIds) {
        Long userId = jwtUtils.getUserId(flag);

        RoadmapManagement roadmapManagement =
                RoadmapManagement.builder()
                        .roadmapNm("Roadmap")
                        .roadmapTimestamp(LocalDateTime.now())
                        .build();

        roadmapManagementRepository.save(roadmapManagement);


        int order = 1;
        for(Long subjectId : subjectIds){
            Subject subject = subjectRepository.findById(subjectId)
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

    public void saveGuestRoadmap(String uuid, String jwt){

        String redisSubjects = redisTemplate.opsForValue().get(uuid);

        if (redisSubjects == null) {
            throw new IllegalArgumentException("Not found roadmap UUID: " + uuid);
        }

        List<Long> subjectIds = Arrays.stream(redisSubjects.split(","))
                .map(Long::parseLong)
                .toList();

        saveRoadmap(jwt, subjectIds);
    }

}

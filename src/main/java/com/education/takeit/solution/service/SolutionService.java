package com.education.takeit.solution.service;

import com.education.takeit.exam.entity.Exam;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.roadmap.entity.Subject;
import com.education.takeit.solution.dto.SolutionResDto;
import com.education.takeit.solution.entity.Solution;
import com.education.takeit.solution.repository.SolutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SolutionService {
    private final SolutionRepository solutionRepository;

    // 해설 조회
    public List<SolutionResDto> findAllUserSolutions(Long userId) {
        List<Solution> solutionList = solutionRepository.findAllByUser_UserId(userId);

        if(solutionList.isEmpty()) {
            throw new CustomException(StatusCode.NOT_FOUND_SOLUTION);
        }

        // TODO : convert로 메소드 분리
        return solutionList.stream()
                .map(solution -> {
                    Exam exam = solution.getExam();
                    Subject subject = exam.getSubject();
                    return new SolutionResDto(
                            solution.isPre(),
                            subject.getSubNm(),           // 과목 이름
                            exam.getExamContent(),        // 문제 내용
                            exam.getOption1(),            // 보기 1
                            exam.getOption2(),            // 보기 2
                            exam.getOption3(),            // 보기 3
                            exam.getOption4(),            // 보기 4
                            exam.getExamAnswer(),         // 정답
                            solution.getUserAnswer(),     // 사용자 선택
                            solution.getSolutionContent(),// 해설
                            exam.getExamLevel()           // 난이도
                    );
                })
                .collect(Collectors.toList());
    }

    // 문제, 사용자id, 사용자 정답 fastapi에 전달해주기!

    // 평가 해설 가져오고 저장

}

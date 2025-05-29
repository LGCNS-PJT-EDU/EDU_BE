package com.education.takeit.interview.service;

import com.education.takeit.interview.dto.InterviewContentResDto;
import com.education.takeit.interview.entity.Interview;
import com.education.takeit.interview.repository.InterviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewService {
    private final InterviewRepository interviewRepository;

    public List<InterviewContentResDto> getInterview(Long subjectId){
        List<Interview> interviewList = interviewRepository.findBySubjectId(subjectId);
        Collections.shuffle(interviewList);
        return interviewList.stream()
                .limit(3)
                .map(i-> new InterviewContentResDto(i.getInterviewContent()))
                .toList();
    }

}

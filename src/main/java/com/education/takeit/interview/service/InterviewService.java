package com.education.takeit.interview.service;

import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.interview.dto.InterviewContentResDto;
import com.education.takeit.interview.entity.Interview;
import com.education.takeit.interview.repository.InterviewRepository;
import com.education.takeit.roadmap.entity.Subject;
import com.education.takeit.roadmap.repository.SubjectRepository;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InterviewService {
  private final InterviewRepository interviewRepository;
  private final SubjectRepository subjectRepository;

  public List<InterviewContentResDto> getInterview(Long subjectId) {
    // subjectId 로 subject 정보 조회
    Subject subject =
        subjectRepository
            .findById(subjectId)
            .orElseThrow(() -> new CustomException(StatusCode.SUBJECT_NOT_FOUND));

    List<Interview> interviewList = interviewRepository.findBySubject(subject);
    Collections.shuffle(interviewList);
    return interviewList.stream()
        .limit(3)
        .map(i -> new InterviewContentResDto(i.getInterviewContent()))
        .toList();
  }
}

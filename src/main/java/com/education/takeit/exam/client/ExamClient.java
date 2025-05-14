package com.education.takeit.exam.client;

import com.education.takeit.exam.dto.ExamResDto;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Component
public class ExamClient {

    private final RestClient restClient;

    public List<ExamResDto> getPreExam(Long userId, Long subjectId) {
        ExamResDto[] arr =
                restClient
                        .get()
                        .uri("http://localhost:8000/api/pre/subject?user_id={userId}&subject_id={subjectId}", userId, subjectId)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .onStatus(
                                status -> !status.is2xxSuccessful(),
                                (request, response) -> {
                                    throw new CustomException(StatusCode.CONNECTION_FAILED);
                                })
                        .body(ExamResDto[].class);

        return Arrays.asList(arr);
    }
}

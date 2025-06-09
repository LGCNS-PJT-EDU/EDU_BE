package com.education.takeit.kafka.recommand.service;

import com.education.takeit.kafka.recommand.dto.RecomFailDto;
import com.education.takeit.kafka.recommand.entity.RecomFailLog;
import com.education.takeit.kafka.recommand.repository.RecomFailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecomFailLogService {

    private final RecomFailRepository repository;

    /**
     * 추천 컨텐츠 생성 실패 로그 적재
     * @param dto
     */
    public void saveFailLog(RecomFailDto dto) {
        RecomFailLog entity =
                RecomFailLog.builder()
                        .userId(dto.userId())
                        .subjectId(dto.subjectId())
                        .errorCode(dto.errorCode())
                        .errorMessage(dto.errorMessage())
                        .build();

        repository.save(entity);
    }
}

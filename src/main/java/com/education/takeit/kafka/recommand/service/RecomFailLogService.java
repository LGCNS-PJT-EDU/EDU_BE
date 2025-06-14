package com.education.takeit.kafka.recommand.service;

import com.education.takeit.admin.failLog.dto.RecomFailLogDto;
import com.education.takeit.admin.failLog.spec.FailLogSpecifications;
import com.education.takeit.kafka.recommand.dto.RecomFailDto;
import com.education.takeit.kafka.recommand.entity.RecomFailLog;
import com.education.takeit.kafka.recommand.repository.RecomFailRepository;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecomFailLogService {

  private final RecomFailRepository recomFailRepository;
  private final UserRepository userRepository;

  /**
   * 추천 컨텐츠 생성 실패 로그 적재
   *
   * @param dto
   */
  @Transactional
  public void saveFailLog(RecomFailDto dto) {
    User user = userRepository.getReferenceById(dto.userId());
    RecomFailLog entity =
        RecomFailLog.builder()
            .user(user)
            .subjectId(dto.subjectId())
            .errorCode(dto.errorCode())
            .errorMessage(dto.errorMessage())
                .retry(false)
            .build();

    recomFailRepository.save(entity);
  }

  @Transactional(readOnly = true)
  public Page<RecomFailLogDto> getPendingFailLogs(
          String nickname,
          String email,
          String errorCode,
          Pageable pageable
  ) {
    Specification<RecomFailLog> spec =
            FailLogSpecifications.searchBy(nickname, email, errorCode);

    return recomFailRepository.findAll(spec, pageable)
            .map(r -> new RecomFailLogDto(
                    r.getId(),
                    r.getUser().getEmail(),
                    r.getUser().getNickname(),
                    r.getSubjectId(),
                    r.getErrorCode(),
                    r.getErrorMessage(),
                    r.getRetry(),
                    r.getCreatedDt()
            ));
  }
}

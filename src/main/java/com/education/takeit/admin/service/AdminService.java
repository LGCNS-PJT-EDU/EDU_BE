package com.education.takeit.admin.service;

import com.education.takeit.admin.dto.AdminSubjectResDto;
import com.education.takeit.admin.dto.TotalUserFindResDto;
import com.education.takeit.roadmap.repository.SubjectRepository;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

  private final UserRepository userRepository;
  private final SubjectRepository subjectRepository;

  public Page<TotalUserFindResDto> getPagedUsers(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<User> userPage = userRepository.findAll(pageable);

    return userPage.map(
        user ->
            new TotalUserFindResDto(
                user.getUserId(),
                user.getEmail(),
                user.getNickname(),
                user.getLoginType(),
                user.getLectureAmount(),
                user.getPriceLevel(),
                user.getActive() != null ? user.getActive() : false,
                Boolean.TRUE.equals(user.getLikesBooks()),
                Boolean.TRUE.equals(user.getPrivacyStatus())));
  }

  public Page<AdminSubjectResDto> getSubjects(String keyword, String sortBy, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    return subjectRepository.findSubjectsWithAssignmentCount(keyword, sortBy, pageable);
  }
}

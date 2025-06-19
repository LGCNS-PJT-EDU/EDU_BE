package com.education.takeit.admin.service;

import com.education.takeit.admin.dto.AdminContentResDto;
import com.education.takeit.admin.dto.AdminExamResDto;
import com.education.takeit.admin.dto.AdminSubjectResDto;
import com.education.takeit.admin.dto.TotalUserFindResDto;
import com.education.takeit.exam.repository.ExamRepository;
import com.education.takeit.recommend.repository.TotalContentRepository;
import com.education.takeit.roadmap.repository.SubjectRepository;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

  private final UserRepository userRepository;
  private final SubjectRepository subjectRepository;
  private final ExamRepository examRepository;
  private final TotalContentRepository totalContentRepository;

  public Page<TotalUserFindResDto> getPagedUsers(
      String nickname, String email, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<User> userPage = userRepository.findByNicknameAndEmail(nickname, email, pageable);

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

  @Transactional(readOnly = true)
  public Page<AdminSubjectResDto> getSubjects(
      String keyword, String sortParam, int page, int size) {
    String[] sortParts = sortParam.split(",");
    String sortBy = sortParts.length > 0 ? sortParts[0] : "id";
    String direction = sortParts.length > 1 ? sortParts[1] : "asc";

    // 정렬 없는 전체 목록 가져오기
    List<AdminSubjectResDto> list = subjectRepository.findSubjectsWithoutOrder(keyword);

    // 자바에서 정렬 적용
    Comparator<AdminSubjectResDto> comparator =
        switch (sortBy) {
          case "count" -> Comparator.comparing(AdminSubjectResDto::assignmentCount);
          case "name" -> Comparator.comparing(
              AdminSubjectResDto::subNm, String.CASE_INSENSITIVE_ORDER);
          case "id" -> Comparator.comparing(AdminSubjectResDto::subId);
          default -> Comparator.comparing(AdminSubjectResDto::subId);
        };

    if (direction.equalsIgnoreCase("desc")) {
      comparator = comparator.reversed();
    }

    list.sort(comparator);

    // 수동 페이징 처리
    int start = (int) Math.min((long) page * size, list.size());
    int end = Math.min(start + size, list.size());
    List<AdminSubjectResDto> paged = list.subList(start, end);

    return new PageImpl<>(paged, PageRequest.of(page, size), list.size());
  }

  @Transactional(readOnly = true)
  public Page<AdminExamResDto> getExams(
      String subName, String examContent, String sortParam, int page, int size) {
    String[] sortParts = sortParam.split(",");
    String sortBy = sortParts.length > 0 ? sortParts[0] : "id";
    String directionRaw = sortParts.length > 1 ? sortParts[1] : "asc";
    boolean isDesc = directionRaw.equalsIgnoreCase("desc");

    List<AdminExamResDto> list =
        examRepository.findExamWithUserCountAndFilter(subName, examContent);

    Comparator<AdminExamResDto> comparator =
        switch (sortBy) {
          case "count" -> Comparator.comparing(AdminExamResDto::userCount);
          case "name" -> Comparator.comparing(
              AdminExamResDto::subName, String.CASE_INSENSITIVE_ORDER);
          case "id" -> Comparator.comparing(AdminExamResDto::examId);
          default -> Comparator.comparing(AdminExamResDto::examId);
        };

    if (isDesc) comparator = comparator.reversed();
    list.sort(comparator);

    int start = Math.min(page * size, list.size());
    int end = Math.min(start + size, list.size());
    List<AdminExamResDto> paged = list.subList(start, end);

    return new PageImpl<>(paged, PageRequest.of(page, size), list.size());
  }

  @Transactional(readOnly = true)
  public Page<AdminContentResDto> getContentList(
      String title, String subName, String sortParam, int page, int size) {
    String[] sortParts = sortParam.split(",");
    String sortBy = sortParts.length > 0 ? sortParts[0] : "id";
    String directionRaw = sortParts.length > 1 ? sortParts[1] : "asc";
    boolean isDesc = directionRaw.equalsIgnoreCase("desc");

    List<AdminContentResDto> list = totalContentRepository.findAllWithoutSort(title, subName);

    Comparator<AdminContentResDto> comparator =
        switch (sortBy) {
          case "count" -> Comparator.comparing(AdminContentResDto::userCount);
          case "title" -> Comparator.comparing(
              AdminContentResDto::contentTitle, String.CASE_INSENSITIVE_ORDER);
          case "id" -> Comparator.comparing(AdminContentResDto::totalContentId);
          default -> Comparator.comparing(AdminContentResDto::totalContentId);
        };

    if (isDesc) comparator = comparator.reversed();
    list.sort(comparator);

    int start = Math.min(page * size, list.size());
    int end = Math.min(start + size, list.size());
    List<AdminContentResDto> paged = list.subList(start, end);

    return new PageImpl<>(paged, PageRequest.of(page, size), list.size());
  }
}

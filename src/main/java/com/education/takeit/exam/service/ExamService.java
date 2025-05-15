package com.education.takeit.exam.service;

import com.education.takeit.exam.dto.*;
import com.education.takeit.global.client.AIClient;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamService {

  private final AIClient aiClient;

  /**
   * 사전 평가 문제 조회
   *
   * @param userId
   * @param subjectId
   * @return
   */
  public List<ExamResDto> findPreExam(Long userId, Long subjectId) {
    /* Fast API: RestClient */
    List<ExamResDto> result = aiClient.getPreExam(userId, subjectId);
    if (result.isEmpty()) {
      throw new CustomException(StatusCode.EMPTY_RESULT);
    }
    return result;
  }

  /**
   * 사전 평가 결과 저장
   *
   * @param userId
   * @param examAnswerRes
   * @return
   */
  public ExamResultDto submitPreExam(Long userId, ExamAnswerResDto examAnswerRes) {
    List<ExamAnswerDto> answers = examAnswerRes.answers();
    // subject 집계
    SubjectResultDto subject = calculateSubjectResult(examAnswerRes);
    // chapter 집계
    List<ChapterResultDto> chapters = calculateChapterResults(answers);

    ExamResultDto result = new ExamResultDto(userId, subject, chapters, answers);

    // FAST API 사전평가 결과 전달
    try {
      // API 호출
    } catch (RestClientException e) {
      // log.warn("FastAPI 통신 실패: {}", e.getMessage());
      // fallback 처리 가능
    }
    // submitCnt update 필요
    return new ExamResultDto(userId, subject, chapters, answers);
  }

  /**
   * 사후 평가 문제 조회
   *
   * @param userId
   * @param subjectId
   * @return
   */
  public List<ExamResDto> findPostExam(Long userId, Long subjectId) {
    /* Fast API: RestClient */
    List<ExamResDto> result = aiClient.getPostExam(userId, subjectId);
    if (result.isEmpty()) {
      throw new CustomException(StatusCode.EMPTY_RESULT);
    }
    return result;
  }

  /**
   * 사후 평가 결과 저장
   *
   * @param userId
   * @param examAnswerRes
   * @return
   */
  public ExamResultDto submitPostExam(Long userId, ExamAnswerResDto examAnswerRes) {
    List<ExamAnswerDto> answers = examAnswerRes.answers();
    // subject 집계
    SubjectResultDto subject = calculateSubjectResult(examAnswerRes);
    // chapter 집계
    List<ChapterResultDto> chapters = calculateChapterResults(answers);

    ExamResultDto result = new ExamResultDto(userId, subject, chapters, answers);

    // FAST API 사전평가 결과 전달
    try {
      // API 호출
    } catch (RestClientException e) {
      // log.warn("FastAPI 통신 실패: {}", e.getMessage());
      // fallback 처리 가능
    }
    // submitCnt update 필요
    return new ExamResultDto(userId, subject, chapters, answers);
  }

  /**
   * 과목별 평가 집계
   *
   * @param examAnswerRes
   * @return
   */
  private SubjectResultDto calculateSubjectResult(ExamAnswerResDto examAnswerRes) {
    List<ExamAnswerDto> answers = examAnswerRes.answers();
    Long subjectId = examAnswerRes.subjectId();
    String startDate = examAnswerRes.startDate();
    Long duration = examAnswerRes.duration();
    int submitCnt = examAnswerRes.submitCnt() + 1;
    int level = calculateLevel(answers);
    int cnt = (int) answers.stream().filter(ExamAnswerDto::answerTF).count();
    int totalCnt = answers.size();

    return new SubjectResultDto(subjectId, startDate, duration, submitCnt, level, cnt, totalCnt);
  }

  /**
   * 과목별 진단 레벨 계산
   *
   * @param answers
   * @return
   */
  private int calculateLevel(List<ExamAnswerDto> answers) {
    int score = answers.stream().mapToInt(this::calculateScoreByDifficulty).sum();

    if (score <= 4) return 1;
    if (score <= 8) return 1;
    if (score <= 12) return 3;
    if (score <= 16) return 4;
    return 5;
  }

  /**
   * 진단 레벨 계산을 위한 난이도별 점수 환산
   *
   * @param answer
   * @return
   */
  private int calculateScoreByDifficulty(ExamAnswerDto answer) {
    if (!answer.answerTF()) return 0;

    return switch (answer.difficulty()) {
      case "하" -> 1;
      case "중" -> 3;
      default -> throw new IllegalArgumentException("지원하지 않는 난이도: " + answer.difficulty());
    };
  }

  /**
   * 단원별 평가 집계
   *
   * @param answers
   * @return
   */
  private List<ChapterResultDto> calculateChapterResults(List<ExamAnswerDto> answers) {
    return answers.stream()
        .collect(Collectors.groupingBy(ExamAnswerDto::chapterNum))
        .entrySet()
        .stream()
        .map(
            entry -> {
              List<ExamAnswerDto> chapterAnswers = entry.getValue();
              String chapterName = chapterAnswers.getFirst().chapterName();
              boolean weakness =
                  chapterAnswers.stream()
                      .anyMatch(dto -> dto.difficulty().equals("하") && !dto.answerTF());
              int cnt = (int) chapterAnswers.stream().filter(ExamAnswerDto::answerTF).count();
              int totalCnt = chapterAnswers.size();
              return new ChapterResultDto(entry.getKey(), chapterName, weakness, cnt, totalCnt);
            })
        .toList();
  }

  public List<ExamResDto> createMock() {
    return List.of(
        ExamResDto.builder()
            .questionId(1)
            .question("HTML 문서의 최상위 루트 요소는 무엇인가?")
            .choice1("<html>")
            .choice2("<head>")
            .choice3("<body>")
            .choice4("<doctype>")
            .answerNum(1)
            .chapterNum(1)
            .chapterName("기본 구조와 시맨틱 태그")
            .difficulty("하")
            .build(),
        ExamResDto.builder()
            .questionId(2)
            .question("head 요소 안에 넣을 수 없는 태그는?")
            .choice1("<title>")
            .choice2("<link>")
            .choice3("<meta>")
            .choice4("<section>")
            .answerNum(4)
            .chapterNum(1)
            .chapterName("기본 구조와 시맨틱 태그")
            .difficulty("하")
            .build(),
        ExamResDto.builder()
            .questionId(3)
            .question("단락을 나타내는 대표적인 블록 요소는?")
            .choice1("<p>")
            .choice2("<span>")
            .choice3("<li>")
            .choice4("<br>")
            .answerNum(1)
            .chapterNum(2)
            .chapterName("텍스트 & 목록 요소")
            .difficulty("하")
            .build(),
        ExamResDto.builder()
            .questionId(4)
            .question("순서 없는 목록을 나타내는 태그는?")
            .choice1("<ul>")
            .choice2("<ol>")
            .choice3("<dl>")
            .choice4("<list>")
            .answerNum(1)
            .chapterNum(2)
            .chapterName("텍스트 & 목록 요소")
            .difficulty("중")
            .build(),
        ExamResDto.builder()
            .questionId(5)
            .question("img 태그의 alt 속성은 어떤 용도인가?")
            .choice1("접근성을 위한 대체 텍스트 제공")
            .choice2("이미지 크기 자동 지정")
            .choice3("CSS 클래스 설정")
            .choice4("SEO 제외 요청")
            .answerNum(1)
            .chapterNum(3)
            .chapterName("이미지·멀티미디어 & IFrame")
            .difficulty("중")
            .build(),
        ExamResDto.builder()
            .questionId(6)
            .question("video 태그에서 controls 속성의 역할은?")
            .choice1("재생 버튼 등을 사용자에게 표시")
            .choice2("자동 재생")
            .choice3("반복 재생")
            .choice4("음소거 재생")
            .answerNum(1)
            .chapterNum(3)
            .chapterName("이미지·멀티미디어 & IFrame")
            .difficulty("하")
            .build(),
        ExamResDto.builder()
            .questionId(7)
            .question("폼에서 사용자의 이메일 형식을 검증하려면 어떤 input 타입을 써야 하는가?")
            .choice1("email")
            .choice2("text")
            .choice3("url")
            .choice4("search")
            .answerNum(1)
            .chapterNum(4)
            .chapterName("폼 & 입력 요소")
            .difficulty("중")
            .build(),
        ExamResDto.builder()
            .questionId(8)
            .question("서로 배타적인 Radio 버튼을 그룹화하려면 동일한 속성은?")
            .choice1("name")
            .choice2("id")
            .choice3("value")
            .choice4("for")
            .answerNum(1)
            .chapterNum(4)
            .chapterName("폼 & 입력 요소")
            .difficulty("하")
            .build(),
        ExamResDto.builder()
            .questionId(9)
            .question("HTML 테이블에서 한 행을 나타내는 요소는?")
            .choice1("<tr>")
            .choice2("<th>")
            .choice3("<td>")
            .choice4("<tbody>")
            .answerNum(1)
            .chapterNum(5)
            .chapterName("테이블·메타데이터 & 접근성")
            .difficulty("중")
            .build(),
        ExamResDto.builder()
            .questionId(10)
            .question("표의 열 제목을 나타내는 시맨틱 태그는?")
            .choice1("<th>")
            .choice2("<td>")
            .choice3("<caption>")
            .choice4("<colgroup>")
            .answerNum(1)
            .chapterNum(5)
            .chapterName("테이블·메타데이터 & 접근성")
            .difficulty("중")
            .build());
  }
}

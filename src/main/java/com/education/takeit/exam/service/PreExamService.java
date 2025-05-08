package com.education.takeit.exam.service;

import com.education.takeit.exam.dto.ExamResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PreExamService {

  public List<ExamResponse> getPreExam() {
    /* Fast API: RestClient */

    /* 임시 목 데이터 생성 */
    List<ExamResponse> result = createMock();

    return result;
  }

  public List<ExamResponse> createMock() {
    return List.of(
        ExamResponse.builder()
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
        ExamResponse.builder()
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
        ExamResponse.builder()
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
        ExamResponse.builder()
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
        ExamResponse.builder()
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
        ExamResponse.builder()
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
        ExamResponse.builder()
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
        ExamResponse.builder()
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
        ExamResponse.builder()
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
        ExamResponse.builder()
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

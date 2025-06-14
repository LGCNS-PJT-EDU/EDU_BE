package com.education.takeit.global.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum StatusCode {
  OK(200, "요청이 성공적으로 처리되었습니다.", HttpStatus.OK),
  ALREADY_EXIST_EMAIL(409, "이미 존재하는 이메일입니다.", HttpStatus.CONFLICT),
  INVALID_SIGNIN_INFO(404, "아이디 또는 비밀번호를 다시 확인해주세요.", HttpStatus.NOT_FOUND),
  NOT_SUPPORT_LOCAL_LOGIN(403, "해당 계정은 소셜로그인 전용입니다.", HttpStatus.FORBIDDEN),
  USER_NOT_FOUND(404, "존재하지 않는 사용자입니다.", HttpStatus.NOT_FOUND),
  INVALID_KAKAO_ID_TOKEN(401, "유효하지 않은 KAKAO ID 토큰입니다.", HttpStatus.UNAUTHORIZED),
  INVALID_GOOGLE_ID_TOKEN(401, "유효하지 않은 GOOGLE ID 토큰입니다.", HttpStatus.UNAUTHORIZED),
  MISSING_NAVER_STATE(401, "NAVER STATE 값이 비어있습니다.", HttpStatus.BAD_REQUEST),
  BAD_REQUEST(400, "잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
  INVALID_TOKEN(401, "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
  UNAUTHORIZED(401, "만료된 토큰입니다.", HttpStatus.UNAUTHORIZED),
  ROADMAP_NOT_FOUND(404, "사용자의 로드맵이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
  DIAGNOSIS_NOT_FOUND(404, "진단 문항이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
  INVALID_DIAGNOSIS_ANSWER(400, "진단 응답이 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
  ROADMAP_TYPE_NOT_FOUND(400, "잘못된 로드맵 생성 요청입니다.", HttpStatus.BAD_REQUEST),
  DEFAULT_ROADMAP_NOT_FOUND(400, "기본 로드맵이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
  CONNECTION_FAILED(502, "서버에 접속할 수 없습니다.", HttpStatus.BAD_GATEWAY),
  SUBJECT_NOT_FOUND(400, "과목 정보를 불러올 수 없습니다", HttpStatus.NOT_FOUND),
  CONTENTS_NOT_FOUND(404, "컨텐츠가 존재하지 않습니다", HttpStatus.NOT_FOUND),
  AI_CONNECTION_FAILED(502, "AI 서버에 접속할 수 없습니다.", HttpStatus.BAD_GATEWAY),
  OAUTH_SERVER_ERROR(502, "OAuth 서버 요청에 실패했습니다.", HttpStatus.BAD_GATEWAY),
  EMPTY_RESULT(204, "응답이 비어 있습니다.", HttpStatus.NO_CONTENT),
  NOT_FOUND_ROADMAP(404, "해당 로드맵 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  DUPLICATED_ROADMAP_MANAGEMENT(409, "사용자에게 중복된 로드맵 관리 정보가 존재합니다.", HttpStatus.CONFLICT),
  ALREADY_EXIST_ROADMAP(409, "이미 로드맵이 존재합니다.", HttpStatus.CONFLICT),
  UNAUTHORIZED_USER(401, "로그인이 필요한 기능입니다.", HttpStatus.UNAUTHORIZED),
  NOT_FOUND_SOLUTION(400, "해설이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
  EXAM_NOT_FOUND(404, "해당 시험이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
  DIAGNOSIS_RESPONSE_NOT_FOUND(404, "진단 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
  ALREADY_EXIST_PRE_EXAM(409, "이미 사전 평가를 응시했습니다.", HttpStatus.CONFLICT),
  INTERVIEW_NOT_FOUND(404, "면접 질문이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
  SUBJECT_ID_REQUIRED(400, "면접 볼 과목을 1개 이상 선택해야 합니다", HttpStatus.BAD_REQUEST),
  NOT_EXIST_SUBJECT(404, "존재하지 않는 과목입니다.", HttpStatus.NOT_FOUND),
  NOT_EXIST_LOG(404, "존재하지 않는 로그입니다.", HttpStatus.NOT_FOUND);

  private final int statusCode;
  private final String message;
  private final HttpStatus httpStatus;

  StatusCode(int statusCode, String message, HttpStatus httpStatus) {
    this.statusCode = statusCode;
    this.message = message;
    this.httpStatus = httpStatus;
  }
}

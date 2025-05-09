package com.education.takeit.exam.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamResDto {
  private int questionId;
  private String question;
  private String choice1;
  private String choice2;
  private String choice3;
  private String choice4;
  private int answerNum;
  private int chapterNum;
  private String chapterName;
  private String difficulty;
}

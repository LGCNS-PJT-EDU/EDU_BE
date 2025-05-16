package com.education.takeit.exam.enums;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Difficulty {
  EASY("하", 1),
  MEDIUM("중", 3),
  HARD("상", 5);

  private final String label;
  private final int score;

  public static Difficulty fromLabel(String label) {
    return Arrays.stream(values())
        .filter(d -> d.label.equals(label))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("알 수 없는 난이도: " + label));
  }
}

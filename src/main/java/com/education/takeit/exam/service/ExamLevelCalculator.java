package com.education.takeit.exam.service;

import com.education.takeit.exam.dto.ExamAnswerDto;
import com.education.takeit.exam.enums.Difficulty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExamLevelCalculator {
    /**
     * 평가 점수 백분율 계산
     * @param answers
     * @return
     */
    public int calculateScorePercent(List<ExamAnswerDto> answers) {
        int score = answers.stream()
                .mapToInt(a -> calculateScoreByDifficulty(a, false))
                .sum();
        int maxScore = answers.stream()
                .mapToInt(a -> calculateScoreByDifficulty(a, true))
                .sum();

        if (maxScore == 0) return 0;
        return (int) ((double) score / maxScore * 100);
    }

    /**
     * 문제 난이도별 점수 계산
     * @param examAnswerDto
     * @param maxCal
     * @return
     */
    public int calculateScoreByDifficulty(ExamAnswerDto examAnswerDto, boolean maxCal) {
        if (!maxCal && !examAnswerDto.answerTF()) return 0;

        Difficulty difficulty = Difficulty.fromLabel(examAnswerDto.difficulty());
        return difficulty.getScore();
    }

    /**
     * 백분율별 진단 레벨 향상 계산
     * @param scorePercent
     * @return
     */
    public int calculateLevelDelta(int scorePercent) {
        if (scorePercent <= 25) return -1;
        if (scorePercent <= 50) return 0;
        if (scorePercent <= 75) return 1;
        return 2;
    }

    /**
     * 진단 레벨 재계산
     * @param currentLevel
     * @param levelDelta
     * @return
     */
    public int calculateNewLevel(int currentLevel, int levelDelta) {
        return Math.max(1, Math.min(5, currentLevel + levelDelta));
    }
}

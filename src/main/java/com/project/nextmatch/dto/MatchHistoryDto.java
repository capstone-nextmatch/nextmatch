//박세준
package com.project.nextmatch.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MatchHistoryDto {

    private final int totalMatches; // 전체 경기 수 (승 + 패)
    private final int wins;         // 승리 횟수
    private final int losses;       // 패배 횟수
    private final double winRate;   // 승률 (0.00 ~ 1.00)

    /**
     * 승률을 계산하고 DTO 객체를 생성하는 정적 팩토리 메서드
     */
    public static MatchHistoryDto calculate(int wins, int losses) {
        int total = wins + losses;
        // 경기 수가 0이면 승률은 0.0, 아니면 (승리 수 / 전체 수)로 계산
        double winRate = (total == 0) ? 0.0 : (double) wins / total;

        // 소수점 셋째 자리에서 반올림하여 둘째 자리까지 표시 (예: 0.666 -> 0.67)
        winRate = Math.round(winRate * 100.0) / 100.0;

        return new MatchHistoryDto(total, wins, losses, winRate);
    }
}
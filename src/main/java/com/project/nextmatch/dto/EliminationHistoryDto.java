//박세준
package com.project.nextmatch.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EliminationHistoryDto {

    private final int eliminationCount; // 예선 탈락 (4등 이하 또는 기록 없음) 횟수

    /**
     * DTO 객체를 생성하는 팩토리 메서드
     */
    public static EliminationHistoryDto create(int count) {
        return new EliminationHistoryDto(count);
    }
}
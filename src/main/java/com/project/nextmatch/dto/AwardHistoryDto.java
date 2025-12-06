//박세준
package com.project.nextmatch.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AwardHistoryDto {

    private final int firstPlaceCount;  // 1등 횟수
    private final int secondPlaceCount; // 2등 횟수
    private final int thirdPlaceCount;  // 3등 횟수
    private final int totalAwards;      // 총 시상 횟수 (1등 + 2등 + 3등)

    /**
     * 시상 횟수를 합산하여 DTO 객체를 생성하는 팩토리 메서드
     */
    public static AwardHistoryDto create(int first, int second, int third) {
        int total = first + second + third;
        return new AwardHistoryDto(first, second, third, total);
    }
}
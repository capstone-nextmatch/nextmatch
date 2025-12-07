//박세준
package com.project.nextmatch.service;

import com.project.nextmatch.dto.AwardHistoryDto;
import com.project.nextmatch.repository.ContestRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AwardService {

    private final ContestRecordRepository contestRecordRepository;

    /**
     * 특정 사용자의 시상 기록 (1, 2, 3위 횟수)을 조회하고 반환합니다.
     * @param memberId 시상 기록을 조회할 사용자 ID
     * @return AwardHistoryDto (시상 기록 정보)
     */
    public AwardHistoryDto getAwardHistory(Long memberId) {
        // 1. Repository를 통해 순위별 횟수를 조회합니다.
        int firstPlaceCount = contestRecordRepository.countByMemberIdAndPlace(memberId, 1);
        int secondPlaceCount = contestRecordRepository.countByMemberIdAndPlace(memberId, 2);
        int thirdPlaceCount = contestRecordRepository.countByMemberIdAndPlace(memberId, 3);

        // 2. DTO의 팩토리 메서드를 사용하여 객체를 생성합니다.
        return AwardHistoryDto.create(firstPlaceCount, secondPlaceCount, thirdPlaceCount);
    }
}
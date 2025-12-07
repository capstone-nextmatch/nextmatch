//박세준
package com.project.nextmatch.service;

import com.project.nextmatch.dto.EliminationHistoryDto;
import com.project.nextmatch.repository.ContestRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EliminationService {

    private final ContestRecordRepository contestRecordRepository;

    private final int ELIMINATION_PLACE_THRESHOLD = 3; // 3등 이하 시상이므로, 4등부터 탈락으로 간주

    /**
     * 특정 사용자의 예선 탈락 기록 (순위 4등 이하)을 조회하고 반환합니다.
     * @param memberId 기록을 조회할 사용자 ID
     * @return EliminationHistoryDto (탈락 횟수 정보)
     */
    public EliminationHistoryDto getEliminationHistory(Long memberId) {
        // 1. Repository를 통해 순위가 3등보다 큰 (즉, 4등 이상의) 횟수를 조회합니다.
        int eliminationCount = contestRecordRepository.countByMemberIdAndPlaceGreaterThan(
                memberId,
                ELIMINATION_PLACE_THRESHOLD // 3보다 큰 순위 (4, 5, ...)
        );

        // 2. DTO의 팩토리 메서드를 사용하여 객체를 생성합니다.
        return EliminationHistoryDto.create(eliminationCount);
    }
}
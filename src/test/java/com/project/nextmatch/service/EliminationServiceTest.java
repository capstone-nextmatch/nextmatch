//박세준
package com.project.nextmatch.service;

import com.project.nextmatch.dto.EliminationHistoryDto;
import com.project.nextmatch.repository.ContestRecordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EliminationServiceTest {

    @InjectMocks
    private EliminationService eliminationService;

    @Mock
    private ContestRecordRepository contestRecordRepository;

    private final Long TEST_MEMBER_ID = 55L;
    private final int ELIMINATION_THRESHOLD = 3; // 4등 이상을 예선 탈락으로 간주

    // --- 1. 일반적인 예선 탈락 횟수 집계 테스트 ---
    @Test
    @DisplayName("16.0_예선_탈락_기록_조회_시_정확한_횟수를_반환한다")
    void getEliminationHistory_shouldReturnCorrectCount() {
        // Given: 7번의 예선 탈락 (4등 이하 순위) 기록이 있다고 가정
        int expectedEliminationCount = 7;

        // Repository Mock 설정
        given(contestRecordRepository.countByMemberIdAndPlaceGreaterThan(
                TEST_MEMBER_ID,
                ELIMINATION_THRESHOLD))
                .willReturn(expectedEliminationCount);

        // When: 서비스 메서드를 호출합니다.
        EliminationHistoryDto result = eliminationService.getEliminationHistory(TEST_MEMBER_ID);

        // Then: 결과 DTO 검증
        assertThat(result.getEliminationCount()).isEqualTo(expectedEliminationCount);

        // Repository 메서드가 올바른 인자로 호출되었는지 검증
        verify(contestRecordRepository).countByMemberIdAndPlaceGreaterThan(TEST_MEMBER_ID, ELIMINATION_THRESHOLD);
    }

    // --- 2. 예선 탈락 기록이 전혀 없을 때 테스트 ---
    @Test
    @DisplayName("17.0_탈락_기록이_없을_때_횟수가_0을_반환한다")
    void getEliminationHistory_shouldReturnZeroWhenNoEliminations() {
        // Given: 탈락 횟수 0으로 가정
        int zeroCount = 0;

        // Repository Mock 설정
        given(contestRecordRepository.countByMemberIdAndPlaceGreaterThan(
                TEST_MEMBER_ID,
                ELIMINATION_THRESHOLD))
                .willReturn(zeroCount);

        // When
        EliminationHistoryDto result = eliminationService.getEliminationHistory(TEST_MEMBER_ID);

        // Then
        assertThat(result.getEliminationCount()).isEqualTo(0);
    }
}
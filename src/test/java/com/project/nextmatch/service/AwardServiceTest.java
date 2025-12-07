//박세준
package com.project.nextmatch.service;

import com.project.nextmatch.dto.AwardHistoryDto;
import com.project.nextmatch.repository.ContestRecordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AwardServiceTest {

    @InjectMocks
    private AwardService awardService;

    @Mock
    private ContestRecordRepository contestRecordRepository;

    private final Long TEST_MEMBER_ID = 42L;

    // --- 1. 일반적인 시상 횟수 집계 테스트 ---
    @Test
    @DisplayName("14.0_시상_기록_조회_시_순위별_횟수와_총_횟수를_정확히_반환한다")
    void getAwardHistory_shouldReturnCorrectCounts() {
        // Given: 1등 3회, 2등 2회, 3등 1회로 가정
        int expectedFirst = 3;
        int expectedSecond = 2;
        int expectedThird = 1;
        int expectedTotal = expectedFirst + expectedSecond + expectedThird;

        // Repository Mock 설정: 순위별 조회 시 반환될 값을 정의합니다.
        given(contestRecordRepository.countByMemberIdAndPlace(TEST_MEMBER_ID, 1))
                .willReturn(expectedFirst);
        given(contestRecordRepository.countByMemberIdAndPlace(TEST_MEMBER_ID, 2))
                .willReturn(expectedSecond);
        given(contestRecordRepository.countByMemberIdAndPlace(TEST_MEMBER_ID, 3))
                .willReturn(expectedThird);

        // When: 서비스 메서드를 호출합니다.
        AwardHistoryDto result = awardService.getAwardHistory(TEST_MEMBER_ID);

        // Then: 결과 DTO 검증
        assertThat(result.getFirstPlaceCount()).isEqualTo(expectedFirst);
        assertThat(result.getSecondPlaceCount()).isEqualTo(expectedSecond);
        assertThat(result.getThirdPlaceCount()).isEqualTo(expectedThird);
        assertThat(result.getTotalAwards()).isEqualTo(expectedTotal);

        // Repository 메서드가 각 순위별로 정확히 한 번씩 호출되었는지 검증합니다.
        verify(contestRecordRepository, times(1)).countByMemberIdAndPlace(TEST_MEMBER_ID, 1);
        verify(contestRecordRepository, times(1)).countByMemberIdAndPlace(TEST_MEMBER_ID, 2);
        verify(contestRecordRepository, times(1)).countByMemberIdAndPlace(TEST_MEMBER_ID, 3);
    }

    // --- 2. 시상 기록이 전혀 없을 때 테스트 ---
    @Test
    @DisplayName("15.0_시상_기록이_없을_때_모든_카운트가_0을_반환한다")
    void getAwardHistory_shouldReturnZeroWhenNoAwards() {
        // Given: 모든 순위 횟수 0으로 가정
        int zeroCount = 0;

        // Repository Mock 설정 (모두 0을 반환하도록 설정)
        given(contestRecordRepository.countByMemberIdAndPlace(TEST_MEMBER_ID, 1)).willReturn(zeroCount);
        given(contestRecordRepository.countByMemberIdAndPlace(TEST_MEMBER_ID, 2)).willReturn(zeroCount);
        given(contestRecordRepository.countByMemberIdAndPlace(TEST_MEMBER_ID, 3)).willReturn(zeroCount);

        // When
        AwardHistoryDto result = awardService.getAwardHistory(TEST_MEMBER_ID);

        // Then
        assertThat(result.getFirstPlaceCount()).isEqualTo(0);
        assertThat(result.getSecondPlaceCount()).isEqualTo(0);
        assertThat(result.getThirdPlaceCount()).isEqualTo(0);
        assertThat(result.getTotalAwards()).isEqualTo(0);
    }
}

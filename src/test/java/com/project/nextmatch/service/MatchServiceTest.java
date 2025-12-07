//박세준
package com.project.nextmatch.service;

import com.project.nextmatch.dto.MatchHistoryDto;
import com.project.nextmatch.repository.MatchResultRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

// Mockito를 사용하여 테스트 대상 객체에 가짜(Mock) 의존성을 주입합니다.
@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    // 테스트 대상 객체 (실제 로직이 있는 MatchService)
    @InjectMocks
    private MatchService matchService;

    // MatchService가 의존하는 Repository를 가짜 객체로 만듭니다.
    @Mock
    private MatchResultRepository matchResultRepository;

    private final Long TEST_MEMBER_ID = 1L;

    // --- 1. 일반적인 승률 계산 테스트 ---
    @Test
    @DisplayName("11.0_10승_5패_일_때_정확한_승리_패배_및_승률을_계산하여_반환한다")
    void getMatchHistoryAndWinRate_shouldReturnCorrectCalculations() {
        // Given: Mock 객체의 동작을 정의합니다. (10승 5패 가정)
        int expectedWins = 10;
        int expectedLosses = 5;
        int expectedTotal = 15;
        // 승률: 10/15 = 0.6666... -> 0.67 (DTO의 반올림 로직 가정)
        double expectedWinRate = 0.67;

        // Repository 메서드 호출 시 반환될 값 설정
        given(matchResultRepository.countWinsByMemberId(TEST_MEMBER_ID))
                .willReturn(expectedWins);
        given(matchResultRepository.countLossesByMemberId(TEST_MEMBER_ID))
                .willReturn(expectedLosses);

        // When: 서비스 메서드를 호출합니다.
        MatchHistoryDto result = matchService.getMatchHistoryAndWinRate(TEST_MEMBER_ID);

        // Then: 결과 검증
        assertThat(result.getTotalMatches()).isEqualTo(expectedTotal);
        assertThat(result.getWins()).isEqualTo(expectedWins);
        assertThat(result.getLosses()).isEqualTo(expectedLosses);
        assertThat(result.getWinRate()).isEqualTo(expectedWinRate);
    }

    // --- 2. 경기 수가 0일 때 테스트 ---
    @Test
    @DisplayName("12.0_경기_수가_0일_때_총_경기_수는_0이고_승률은_0.0을_반환한다")
    void getMatchHistoryAndWinRate_shouldReturnZeroWhenNoMatches() {
        // Given: 승/패 모두 0회로 가정합니다.
        given(matchResultRepository.countWinsByMemberId(TEST_MEMBER_ID))
                .willReturn(0);
        given(matchResultRepository.countLossesByMemberId(TEST_MEMBER_ID))
                .willReturn(0);

        // When: 서비스 메서드를 호출합니다.
        MatchHistoryDto result = matchService.getMatchHistoryAndWinRate(TEST_MEMBER_ID);

        // Then: 결과 검증
        assertThat(result.getTotalMatches()).isEqualTo(0);
        assertThat(result.getWins()).isEqualTo(0);
        assertThat(result.getLosses()).isEqualTo(0);
        assertThat(result.getWinRate()).isEqualTo(0.0);
    }

    // --- 3. 1승 0패일 때 테스트 (100% 승률) ---
    @Test
    @DisplayName("13.0_1승_0패_일_때_승률이_1.00인지_확인한다")
    void getMatchHistoryAndWinRate_shouldReturnOnePointZeroForOneWin() {
        // Given: 1승 0패 가정
        given(matchResultRepository.countWinsByMemberId(TEST_MEMBER_ID))
                .willReturn(1);
        given(matchResultRepository.countLossesByMemberId(TEST_MEMBER_ID))
                .willReturn(0);

        // When: 서비스 메서드를 호출합니다.
        MatchHistoryDto result = matchService.getMatchHistoryAndWinRate(TEST_MEMBER_ID);

        // Then: 결과 검증
        assertThat(result.getTotalMatches()).isEqualTo(1);
        assertThat(result.getWins()).isEqualTo(1);
        assertThat(result.getLosses()).isEqualTo(0);
        assertThat(result.getWinRate()).isEqualTo(1.00); // 1.0을 반환해야 합니다.
    }
}

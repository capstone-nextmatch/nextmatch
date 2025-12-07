package com.project.nextmatch.service;

import com.project.nextmatch.domain.Match;
import com.project.nextmatch.dto.MatchHistoryDto;
import com.project.nextmatch.dto.MatchResultRequest;
import com.project.nextmatch.repository.MatchRepository;
import com.project.nextmatch.repository.MatchResultRepository; // 박세준 로직을 위해 추가
import com.project.nextmatch.repository.RoundRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchService {

    // 권동혁 로직 의존성
    private final MatchRepository matchRepository;
    private final RoundRepository roundRepository;
    private final RoundService roundService;

    // 박세준 로직 의존성
    private final MatchResultRepository matchResultRepository;


    /**
     * 경기 결과 목록을 제출하고, 승자를 결정하며, 다음 라운드 생성을 시도합니다. (권동혁 로직)
     * @param results 경기 결과 목록 DTO
     */
    @Transactional
    public void submitMatchResults(List<MatchResultRequest> results) {
        for (MatchResultRequest result : results) {
            Match match = matchRepository.findById(result.getMatchId())
                    .orElseThrow(() -> new EntityNotFoundException("경기를 찾을 수 없습니다. ID=" + result.getMatchId()));

            if (result.getScore1() == null || result.getScore2() == null) {
                throw new IllegalArgumentException("점수가 누락되었습니다.");
            }

            match.setScore1(result.getScore1());
            match.setScore2(result.getScore2());

            // 승자 결정
            if (result.getScore1() > result.getScore2()) {
                match.setWinner(match.getPlayer1());
            } else if (result.getScore2() > result.getScore1()) {
                match.setWinner(match.getPlayer2());
            } else {
                throw new IllegalArgumentException("무승부는 허용되지 않습니다.");
            }

            matchRepository.save(match);
        }

        // 모든 경기 완료 여부 확인 및 다음 라운드 생성
        roundService.checkAndCreateNextRound(results);
    }

    /**
     * 특정 사용자의 전적을 조회하고 승률을 계산하여 반환합니다. (박세준 로직)
     * @param memberId 전적을 조회할 사용자 ID
     * @return MatchHistoryDto (전적 및 승률 정보)
     */
    @Transactional(readOnly = true)
    public MatchHistoryDto getMatchHistoryAndWinRate(Long memberId) {
        // 1. Repository를 통해 승리 및 패배 횟수를 조회
        int wins = matchResultRepository.countWinsByMemberId(memberId);
        int losses = matchResultRepository.countLossesByMemberId(memberId);

        // 2. DTO의 정적 팩토리 메서드를 사용하여 승률을 계산하고 객체를 생성
        return MatchHistoryDto.calculate(wins, losses);
    }
}
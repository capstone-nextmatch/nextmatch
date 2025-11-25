package com.project.nextmatch.service;

import com.project.nextmatch.domain.Contest;
import com.project.nextmatch.domain.Match;
import com.project.nextmatch.dto.MatchResultRequest;
import com.project.nextmatch.repository.ContestRepository;
import com.project.nextmatch.repository.MatchRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AdminMatchService {

    private final MatchRepository matchRepository;
    private final ContestRepository contestRepository;

    @Transactional
    public void recordMatchResult(Long matchId, MatchResultRequest request) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new NoSuchElementException("Match not found with ID: " + matchId));

        // 🛑 ADM_T_005: 이미 완료된 매치 재입력 차단 (추가 로직)
        if ("FINISHED".equalsIgnoreCase(match.getStatus()) || "WALKOVER".equalsIgnoreCase(match.getStatus())) {
            throw new IllegalStateException("Match ID " + matchId + " is already finished and cannot be modified.");
        }

        String winnerTeamName = getWinnerTeamName(match, request.getScoreA(), request.getScoreB());

        // 1. 대진표 반영 로직을 위한 Contest 정보 로드
        Contest contest = contestRepository.findById(match.getContestId())
                .orElseThrow(() -> new NoSuchElementException("Contest not found for Match ID: " + matchId));

        if ("TOURNAMENT".equalsIgnoreCase(contest.getFormat()) && "DRAW".equals(winnerTeamName)) {
            // 🛑 ADM_T_004: 토너먼트 무승부 입력 예외 처리
            throw new IllegalArgumentException("Draws are not allowed in TOURNAMENT format matches.");
        }

        // 2. 경기 상태 및 점수 업데이트 (DB 반영)
        match.updateResult(
                request.getScoreA(),
                request.getScoreB(),
                "FINISHED" // 상태를 완료로 변경
        );
        matchRepository.save(match);


        // 3. 대진표 반영 로직 실행
        if ("TOURNAMENT".equalsIgnoreCase(contest.getFormat())) {
            // ADM_T_002, ADM_T_003을 포함하는 다음 라운드 진출 로직
            advanceWinnerToNextRound(match, winnerTeamName);

        } else if ("LEAGUE".equalsIgnoreCase(contest.getFormat())) {
            // 리그 로직: 순위표 점수 업데이트
            handleLeagueStandingUpdate(match, winnerTeamName);
        }
    }

    /**
     * ADM_T_002, ADM_T_003 검증을 위한 승자 진출 핵심 메서드.
     * MatchRepository에 updateTeamSlot, nextMatchId 필드가 Match 엔티티에 있다고 가정함.
     */
    public void advanceWinnerToNextRound(Match currentMatch, String winnerTeamName) {
        // 🛑 ADM_T_008: 결승전 결과 입력 후 로직 종료
        if (currentMatch.getNextMatchId() == null) {
            return; // 결승전이므로 종료
        }

        if ("DRAW".equals(winnerTeamName)) {
            // 무승부는 이미 recordMatchResult에서 예외 처리됨. 여기서는 안전하게 종료.
            return;
        }

        // 1. 다음 매치 로드
        Long nextMatchId = currentMatch.getNextMatchId();
        Match nextMatch = matchRepository.findById(nextMatchId)
                .orElseThrow(() -> new NoSuchElementException("Next match not found with ID: " + nextMatchId));

        // 🛑 ADM_T_005 (확장): 다음 라운드가 이미 진행 완료(FINISHED) 상태라면 이전 라운드 수정 차단
        if ("FINISHED".equalsIgnoreCase(nextMatch.getStatus()) || "WALKOVER".equalsIgnoreCase(nextMatch.getStatus())) {
            throw new IllegalStateException("Cannot update previous match result as next match is already processed.");
        }

        // 2. 승자 슬롯 결정 및 🛑 ADM_T_009/ADM_T_012 (슬롯 대칭성 및 오버라이드 방지)
        boolean isSlotA;

        // 💡 [가정 로직]: 매치 ID가 홀수면 다음 매치 Team A 슬롯, 짝수면 Team B 슬롯에 넣는다고 가정.
        // 실제 구현에서는 매치 순서, 라운드 번호 등으로 정확한 슬롯을 결정해야 함.
        if (currentMatch.getId() % 2 != 0) {
            isSlotA = true;
        } else {
            isSlotA = false;
        }

        if (isSlotA) {
            if (nextMatch.getTeamA() != null) return; // ADM_T_012: 슬롯이 이미 채워져 있으면 오버라이드 방지
            nextMatch.updateTeamSlot(winnerTeamName, true);
        } else {
            if (nextMatch.getTeamB() != null) return; // ADM_T_012: 슬롯이 이미 채워져 있으면 오버라이드 방지
            nextMatch.updateTeamSlot(winnerTeamName, false);
        }

        matchRepository.save(nextMatch);
    }

    // 헬퍼: 승리팀 이름을 결정하는 로직
    private String getWinnerTeamName(Match match, Integer scoreA, Integer scoreB) {
        if (scoreA > scoreB) {
            return match.getTeamA();
        } else if (scoreB > scoreA) {
            return match.getTeamB();
        }
        return "DRAW"; // 무승부 처리
    }

    // 헬퍼: 리그 순위표 업데이트 로직 (TODO)
    private void handleLeagueStandingUpdate(Match match, String winnerTeamName) {
        // TODO: 리그 순위표(`Standing` 테이블 등)를 업데이트하는 로직 구현 필요
        // 순위표 테이블이 없으므로 현재는 생략
    }
}

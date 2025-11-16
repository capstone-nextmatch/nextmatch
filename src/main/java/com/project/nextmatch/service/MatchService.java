package com.project.nextmatch.service;

import com.project.nextmatch.domain.Contest;
import com.project.nextmatch.domain.Match;
import com.project.nextmatch.domain.Player;
import com.project.nextmatch.domain.Round;
import com.project.nextmatch.dto.MatchResultRequest;
import com.project.nextmatch.repository.MatchRepository;
import com.project.nextmatch.repository.RoundRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final RoundRepository roundRepository;
    private final RoundService roundService;

    @Transactional
    public void submitMatchResults(List<MatchResultRequest> results) {
        for (MatchResultRequest result : results) {
            Match match = matchRepository.findById(result.getMatchId())
                    .orElseThrow(() -> new EntityNotFoundException("경기를 찾을 수 없습니다. ID=" + result.getMatchId()));

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

        // 모든 경기 완료 여부 확인
        roundService.checkAndCreateNextRound(results);
    }
}

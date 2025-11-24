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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class RoundService {

    private final MatchRepository matchRepository;
    private final RoundRepository roundRepository;

    public void checkAndCreateNextRound(List<MatchResultRequest> results) {
        // 같은 라운드의 경기들을 확인
        Long roundId = matchRepository.findById(results.get(0).getMatchId())
                .orElseThrow(() -> new EntityNotFoundException("경기를 찾을 수 없습니다."))
                .getRound().getId();

        Round round = roundRepository.findById(roundId)
                .orElseThrow(() -> new EntityNotFoundException("라운드를 찾을 수 없습니다."));

        List<Match> matches = matchRepository.findByRound(round);

        boolean allCompleted = matches.stream().allMatch(m -> m.getWinner() != null);

        if (allCompleted) {
            // 승자 모으기
            List<Player> winners = matches.stream()
                    .map(Match::getWinner)
                    .collect(Collectors.toList());

            Contest contest = round.getContest();

            Round nextRound = roundRepository.save(Round.builder()
                    .contest(contest)
                    .roundNumber(round.getRoundNumber() + 1)
                    .name(getRoundName(winners.size()))
                    .build());

            for (int i = 0; i + 1 < winners.size(); i += 2) {
                Match nextMatch = Match.builder()
                        .player1(winners.get(i))
                        .player2(winners.get(i + 1))
                        .round(nextRound)
                        .build();
                matchRepository.save(nextMatch);
            }
        }
    }

    public String getRoundName(int size) {
        switch (size) {
            case 64: return "64강";
            case 32: return "32강";
            case 16: return "16강";
            case 8:  return "8강";
            case 4:  return "4강";
            case 2:  return "결승";
            default: return size + "인 라운드";
        }
    }
}

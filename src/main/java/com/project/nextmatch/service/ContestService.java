//권동혁

package com.project.nextmatch.service;

import com.project.nextmatch.domain.*;
import com.project.nextmatch.dto.ContestCreateRequest;
import com.project.nextmatch.dto.MatchCreateRequest;
import com.project.nextmatch.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import java.awt.geom.RoundRectangle2D;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Getter
@RequiredArgsConstructor
public class ContestService {

    private final PlayerRepository playerRepository;
    private final MemberRepository memberRepository;
    private final ContestRepository contestRepository;
    private final MatchRepository matchRepository;
    private final RoundRepository roundRepository;
    private final RoundService roundService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Contest contestCreate(ContestCreateRequest request) {
        Member member = memberRepository.findByUsername(request.getUsername()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        if (request.getStartDate().isAfter(request.getDeadlineDate())) {
            throw new IllegalArgumentException("시작일은 마감일보다 이전이어야 합니다");
        }

        //입력값 정화
        String safeTitle = Jsoup.clean(request.getTitle(), Safelist.basic());
        String safeDescription = Jsoup.clean(request.getDescription(), Safelist.basic());
        String safeCategory = Jsoup.clean(request.getContestCategory(), Safelist.basic());
        String safeImageUrl = Jsoup.clean(request.getImageUrl(), Safelist.basic());


        //대회 생성
        Contest contest = Contest.builder()
                .member(member)
                .eventCategory(safeCategory)
                .imageUrl(safeImageUrl)
                .title(safeTitle)
                .description(safeDescription)
                .startDate(request.getStartDate())
                .deadlineDate(request.getDeadlineDate())
                .build();

        return contestRepository.save(contest);
    };

    public void createAllMatches(MatchCreateRequest request) {

        List<Long> memberIds = request.getMemberId();

        Set<Long> uniqueIds = new HashSet<>(memberIds);
        if (uniqueIds.size() != memberIds.size()) {
            throw new IllegalArgumentException("중복된 참가자가 포함되어 있습니다.");
        }

        // 참가자 → Player 조회
        List<Player> players = playerRepository.findAllByMemberIdInAndContestId(
                request.getMemberId(), request.getContestId());

        // Map으로 변환 (key: memberId, value: Player)
        Map<Long, Player> playerMap = players.stream()
                .collect(Collectors.toMap(p -> p.getMember().getId(), p -> p));

        // 입력된 순서대로 다시 정렬
        List<Player> orderedPlayers = memberIds.stream()
                .map(playerMap::get)
                .collect(Collectors.toList());



        if (players.size() % 2 != 0) {
            throw new IllegalArgumentException("참가자 수는 짝수여야 합니다.");
        }

        // Contest 조회
        Contest contest = contestRepository.findById(request.getContestId())
                .orElseThrow(() -> new EntityNotFoundException("해당 대회가 존재하지 않습니다."));



        // 첫 라운드 생성
        Round round = roundRepository.save(Round.builder()
                .contest(contest)
                .roundNumber(1)
                .name(roundService.getRoundName(orderedPlayers.size())) // "32강" 등
                .build());


        for (int i = 0; i < orderedPlayers.size(); i += 2) {
            Match match = Match.builder()
                    .player1(orderedPlayers.get(i))
                    .player2(orderedPlayers.get(i + 1))
                    .round(round)
                    .build();
            matchRepository.save(match);
        }
    }
}

//권동혁

package com.project.nextmatch.service;

import com.project.nextmatch.domain.*;
import com.project.nextmatch.dto.ContestCreateRequest;
import com.project.nextmatch.dto.MatchCreateRequest;
import com.project.nextmatch.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional; // Spring의 Transactional 사용 통일
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup; // 입력값 정화 로직을 위해 추가
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation; // Propagation 사용을 위해 추가

import java.util.*;
import java.util.stream.Collectors;

@Service
@Getter
@RequiredArgsConstructor
public class ContestService {

    // 모든 Repository 및 Service 의존성 주입
    private final PlayerRepository playerRepository;
    private final MemberRepository memberRepository;
    private final ContestRepository contestRepository;
    private final MatchRepository matchRepository;
    private final RoundRepository roundRepository;
    private final RoundService roundService;


    // Contest ID로 조회 (두 번째 파일의 readOnly 트랜잭션 유지)
    @Transactional(readOnly = true)
    public Contest findContestById(Long id) {
        // ContestRepository를 사용하여 ID 조회, 데이터가 없으면 IllegalArgumentException 발생
        return contestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contest not found with id: " + id));
    }

    // 대회 생성 (두 번째 파일의 유효성 검사, Jsoup 정화, REQUIRES_NEW 트랜잭션 유지)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Contest contestCreate(ContestCreateRequest request) {
        // 1. 회원 존재 여부 확인
        Member member = memberRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 2. 날짜 유효성 검증
        if (request.getStartDate().isAfter(request.getDeadlineDate())) {
            throw new IllegalArgumentException("시작일은 마감일보다 이전이어야 합니다");
        }

        // 3. 입력값 정화 (Jsoup 사용)
        String safeTitle = Jsoup.clean(request.getTitle(), Safelist.basic());
        String safeDescription = Jsoup.clean(request.getDescription(), Safelist.basic());
        // ContestCreateRequest DTO 병합 결과에 따라 eventCategory 대신 contestCategory를 사용합니다.
        String safeCategory = Jsoup.clean(request.getContestCategory(), Safelist.basic());
        String safeImageUrl = Jsoup.clean(request.getImageUrl(), Safelist.basic());


        // 4. 대회 생성
        Contest contest = Contest.builder()
                .member(member)
                .eventCategory(safeCategory) // DTO 병합 결과: contestCategory -> eventCategory로 매핑
                .imageUrl(safeImageUrl)
                .title(safeTitle)
                .description(safeDescription)
                .startDate(request.getStartDate())
                .deadlineDate(request.getDeadlineDate())
                .build();

        // 5. 저장 후 엔티티 반환 (두 번째 파일 로직)
        return contestRepository.save(contest);
    }

    // 매치 및 라운드 생성 로직 (두 번째 파일의 핵심 기능)
    @Transactional // 매치 생성 로직은 트랜잭션이 필요합니다.
    public void createAllMatches(MatchCreateRequest request) {

        List<Long> memberIds = request.getMemberId();

        // 1. 참가자 중복 검사
        Set<Long> uniqueIds = new HashSet<>(memberIds);
        if (uniqueIds.size() != memberIds.size()) {
            throw new IllegalArgumentException("중복된 참가자가 포함되어 있습니다.");
        }

        // 2. 참가자 수 짝수 검사 (ContestController에서 검사하지만, 서비스에서도 안전성 확보)
        if (memberIds.size() % 2 != 0) {
            throw new IllegalArgumentException("참가자 수는 짝수여야 합니다.");
        }

        // 3. 참가자 → Player 조회 및 정렬
        List<Player> players = playerRepository.findAllByMemberIdInAndContestId(
                request.getMemberId(), request.getContestId());

        Map<Long, Player> playerMap = players.stream()
                .collect(Collectors.toMap(p -> p.getMember().getId(), p -> p));

        List<Player> orderedPlayers = memberIds.stream()
                .map(playerMap::get)
                .collect(Collectors.toList());


        // 4. Contest 조회
        Contest contest = contestRepository.findById(request.getContestId())
                .orElseThrow(() -> new EntityNotFoundException("해당 대회가 존재하지 않습니다."));


        // 5. 첫 라운드 생성
        Round round = roundRepository.save(Round.builder()
                .contest(contest)
                .roundNumber(1)
                .name(roundService.getRoundName(orderedPlayers.size())) // "32강" 등
                .build());


        // 6. 매치 생성 (i와 i+1을 묶어 매치 생성)
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
package com.project.nextmatch.service;

import com.project.nextmatch.domain.*;
import com.project.nextmatch.dto.MatchResultRequest;
import com.project.nextmatch.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class MatchServiceTest {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContestService contestService;

    @Autowired
    private ContestRepository contestRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private RoundRepository roundRepository;

    @Autowired
    private RoundService roundService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private Validator validator;

    private Contest contest;
    private List<Member> members;


    @AfterEach
    void cleanUp() {
        matchRepository.deleteAll();   // Match 먼저 삭제
        roundRepository.deleteAll();   // Round 삭제
        playerRepository.deleteAll();  // Player 삭제
        contestRepository.deleteAll(); // Contest 삭제
        memberRepository.deleteAll();  // Member 삭제

    }

    @BeforeEach
    void setUp() {
        // 테스트용 Member 16명 생성
        members = new ArrayList<>();
        for (int i = 1; i <= 16; i++) {
            Member member = memberRepository.save(
                    Member.builder()
                            .username("user" + i)
                            .password("password" + i)
                            .build()
            );
            members.add(member);
        }

        // 테스트용 Contest 생성 (작성자 Member 하나 지정)
        contest = contestRepository.save(
                Contest.builder()
                        .title("테스트 대회")
                        .member(members.get(0)) // 대회 생성자
                        .eventCategory("축구")
                        .status("대기중")
                        .description("테스트용 대회입니다.")
                        .build()
        );


    }



    //권동혁
    @Test
    @Transactional
    @DisplayName("1. 경기 생성 성공 테스트 - 16명 참가자")
    void createMatch_success() throws Exception {
        // given: 16명의 memberId와 contestId
        List<Long> memberIds = members.stream()
                .map(Member::getId)
                .collect(Collectors.toList());

        String requestJson = """
            {
              "memberId": %s,
              "contestId": %d
            }
            """.formatted(memberIds, contest.getId());

        // when & then
        mockMvc.perform(post("/api/event/create/matches")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string("각 경기생성이 완료되었습니다."));

        // 👇 로그 출력
        matchRepository.findAll().forEach(m ->
                log.info("Match ID={}, Player1={}, Player2={}, Round={}",
                        m.getId(),
                        m.getPlayer1().getId(),
                        m.getPlayer2().getId(),
                        m.getRound().getId())
        );
    }

    @Test
    @DisplayName("2. 경기 생성 실패 테스트 - 잘못된 MemberID 포함")
    void createMatch_fail_invalidMemberId() throws Exception {
        // given: 존재하지 않는 Member ID 포함
        List<Long> memberIds = members.stream()
                .map(Member::getId)
                .collect(Collectors.toList());
        memberIds.set(0, 99999L); // 첫 번째 ID를 가짜로 치환

        String requestJson = """
        {
          "memberId": %s,
          "contestId": %d
        }
        """.formatted(memberIds, contest.getId());

        // when & then
        mockMvc.perform(post("/api/event/create/matches")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound()) // 예외 핸들러에서 반환한 상태코드
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString("회원이 존재하지 않습니다")));

    }

    @Test
    @DisplayName("3. 경기 생성 실패 테스트 - 잘못된 ContestID")
    void createMatch_fail_invalidContestId() throws Exception {
        // given: 가짜 contestId
        long invalidContestId = 987654321L;

        List<Long> memberIds = members.stream()
                .map(Member::getId)
                .collect(Collectors.toList());

        String requestJson = """
        {
          "memberId": %s,
          "contestId": %d
        }
        """.formatted(memberIds, invalidContestId);

        // when & then
        mockMvc.perform(post("/api/event/create/matches")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound()) // 404 기대
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString("해당 대회가 존재하지 않습니다.")));

    }

    @Test
    @DisplayName("4. 경기 생성 실패 테스트 - 중복 MemberID 포함")
    void createMatch_fail_duplicateMemberIds() throws Exception {
        // given: 중복 ID 포함
        List<Long> memberIds = members.stream()
                .map(Member::getId)
                .collect(Collectors.toList());

        // 예: 0번, 1번을 동일 ID로 만들기
        Long duplicateId = memberIds.get(0);
        memberIds.set(1, duplicateId);

        String requestJson = """
        {
          "memberId": %s,
          "contestId": %d
        }
        """.formatted(memberIds, contest.getId());

        // when & then
        mockMvc.perform(post("/api/event/create/matches")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("중복된 참가자")));
    }

    @Test
    @DisplayName("5.1. 경기 결과 입력 성공 - Player1 승리")
    void submitResults_success_player1Win() throws Exception {
        // given: 테스트용 Player와 Match 생성
        Player p1 = playerRepository.save(Player.builder()
                .member(members.get(0))
                .contest(contest)
                .build());

        Player p2 = playerRepository.save(Player.builder()
                .member(members.get(1))
                .contest(contest)
                .build());

        Round round = roundRepository.save(Round.builder()
                .contest(contest)
                .roundNumber(1)
                .build());

        Match match = matchRepository.save(Match.builder()
                .player1(p1)
                .player2(p2)
                .round(round)
                .build());

        String requestJson = """
        [
          {
            "matchId": %d,
            "score1": 3,
            "score2": 1
          }
        ]
        """.formatted(match.getId());

        // when & then
        mockMvc.perform(post("/api/matches/results")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string("경기 결과가 저장되었습니다."));

        // DB에서 다시 조회 후 승자 검증
        Match updated = matchRepository.findById(match.getId()).orElseThrow();
        assertNotNull(updated.getWinner(), "승자가 저장되어야 합니다.");
        assertEquals(updated.getPlayer1().getId(), updated.getWinner().getId(),
                "Player1이 승자로 저장되어야 합니다.");
    }

    @Test
    @DisplayName("5.2. 경기 결과 입력 실패 - 무승부 불가")
    void submitResults_fail_draw() throws Exception {
        // given: 테스트용 Player와 Match 생성
        Player p1 = playerRepository.save(Player.builder()
                .member(members.get(0))
                .contest(contest)
                .build());

        Player p2 = playerRepository.save(Player.builder()
                .member(members.get(1))
                .contest(contest)
                .build());

        Round round = roundRepository.save(Round.builder()
                .contest(contest)
                .roundNumber(1)
                .build());

        Match match = matchRepository.save(Match.builder()
                .player1(p1)
                .player2(p2)
                .round(round)
                .build());

        String requestJson = """
    [
      {
        "matchId": %d,
        "score1": 2,
        "score2": 2
      }
    ]
    """.formatted(match.getId());

        // when & then
        mockMvc.perform(post("/api/matches/results")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString("무승부는 허용되지 않습니다.")));
    }

    @Test
    @DisplayName("6. 참가자 수 부족 시 실패")
    void createMatch_fail_notEnoughPlayers() throws Exception {
        // given: 참가자 1명만 전달
        List<Long> memberIds = members.subList(0, 1).stream()
                .map(Member::getId)
                .toList();

        String requestJson = """
    {
      "memberId": %s,
      "contestId": %d
    }
    """.formatted(memberIds, contest.getId());

        // when & then
        mockMvc.perform(post("/api/event/create/matches")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("7. 참가자 수 홀수 시 실패")
    void createMatch_fail_oddNumberOfPlayers() throws Exception {
        // given: 15명만 전달 (홀수)
        List<Long> memberIds = members.subList(0, 15).stream()
                .map(Member::getId)
                .toList();

        String requestJson = """
    {
      "memberId": %s,
      "contestId": %d
    }
    """.formatted(memberIds, contest.getId());

        // when & then
        mockMvc.perform(post("/api/event/create/matches")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString("참가자 수가 홀수입니다")));
    }


    @Test
    @DisplayName("8. 잘못된 MatchID로 경기 결과 입력 실패")
    void submitResults_fail_invalidMatchId() throws Exception {
        // given: 존재하지 않는 Match ID
        long invalidMatchId = 999999L;

        String requestJson = """
    [
      {
        "matchId": %d,
        "score1": 1,
        "score2": 0
      }
    ]
    """.formatted(invalidMatchId);

        // when & then
        mockMvc.perform(post("/api/matches/results")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString("경기를 찾을 수 없습니다")));
    }

    @Test
    @DisplayName("9. 경기 결과 점수 누락 시 실패")
    void submitResults_fail_missingScore() throws Exception {
        // given: Match 생성
        Player p1 = playerRepository.save(Player.builder()
                .member(members.get(0))
                .contest(contest)
                .build());

        Player p2 = playerRepository.save(Player.builder()
                .member(members.get(1))
                .contest(contest)
                .build());

        Round round = roundRepository.save(Round.builder()
                .contest(contest)
                .roundNumber(1)
                .build());

        Match match = matchRepository.save(Match.builder()
                .player1(p1)
                .player2(p2)
                .round(round)
                .build());

        // score2 누락
        String requestJson = """
    [
      {
        "matchId": %d,
        "score1": 3
      }
    ]
    """.formatted(match.getId());

        // when & then
        mockMvc.perform(post("/api/matches/results")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString("점수가 누락되었습니다")));
    }

    @Test
    @DisplayName("10. 다음 라운드 자동 생성 검증")
    void createMatch_success_nextRoundGenerated() throws Exception {
        // given: 16명의 memberId와 contestId
        List<Long> memberIds = members.stream()
                .map(Member::getId)
                .collect(Collectors.toList());

        String requestJson = """
                {
                  "memberId": %s,
                  "contestId": %d
                }
                """.formatted(memberIds, contest.getId());

        // when & then
        mockMvc.perform(post("/api/event/create/matches")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string("각 경기생성이 완료되었습니다."));

        // 👇 로그 출력
        matchRepository.findAll().forEach(m ->
                log.info("Match ID={}, Player1={}, Player2={}, Round={}",
                        m.getId(),
                        m.getPlayer1().getId(),
                        m.getPlayer2().getId(),
                        m.getRound().getId())
        );

        // 3️⃣ 모든 경기 결과 입력
        List<MatchResultRequest> results = matchRepository.findAll().stream()
                .map(m -> {
                    MatchResultRequest dto = new MatchResultRequest();
                    dto.setMatchId(m.getId());
                    dto.setScore1(3); // player1 승리
                    dto.setScore2(1);
                    return dto;
                })
                .toList();

        matchService.submitMatchResults(results);

        // 4️⃣ 다음 라운드 생성 여부 검증
        List<Round> rounds = roundRepository.findAll();
        rounds.forEach(r -> log.info("Round ID={}, Round Number={}", r.getId(), r.getRoundNumber()));

        assertTrue(rounds.size() > 1, "다음 라운드가 생성되어야 합니다.");
    }

}
package com.project.nextmatch.service;

import com.project.nextmatch.domain.Contest;
import com.project.nextmatch.domain.Member;
import com.project.nextmatch.domain.Round;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class KwonMatchServiceTest {
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
    @DisplayName("경기 생성 성공 테스트 - 16명 참가자")
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
//이병철
package com.project.nextmatch;

import com.project.nextmatch.domain.Contest;
import com.project.nextmatch.domain.Member;
import com.project.nextmatch.domain.Match; // 🛑 Match 엔티티 import 추가
import com.project.nextmatch.repository.ContestRepository;
import com.project.nextmatch.repository.MemberRepository;
import com.project.nextmatch.repository.MatchRepository; // 🛑 MatchRepository import 추가

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final ContestRepository contestRepository;
    private final MatchRepository matchRepository; // 🛑 MatchRepository 주입
    // private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        // 1. 테스트 회원 생성 및 저장
        Member testMember = Member.builder()
                .username("testuser")
                .password("password123")
                .build();

        memberRepository.save(testMember);

        // 2. 테스트 대회 생성 및 저장 (Match 테이블과 연동을 위해 Contest 객체 저장)

        // 2-1. 토너먼트 형식 대회
        Contest contestTournament = contestRepository.save(Contest.builder()
                .member(testMember)
                .title("제1회 NextMatch 축구 토너먼트") // 토너먼트임을 명시
                .eventCategory("축구")
                .status("ONGOING")
                .imageUrl("https://example.com/image1.jpg")
                .description("미니 축구 토너먼트입니다.")
                .startDate(LocalDate.now().plusDays(10))
                .deadlineDate(LocalDate.now().plusDays(5))
                .format("TOURNAMENT") // 🛑 형식 추가
                .build());

        // 2-2. 리그 형식 대회
        Contest contestLeague = contestRepository.save(Contest.builder()
                .member(testMember)
                .title("2025 배드민턴 동호회 리그")
                .eventCategory("배드민턴")
                .status("UPCOMING")
                .imageUrl("https://example.com/image2.jpg")
                .description("경쟁적인 리그전입니다.")
                .startDate(LocalDate.now().plusMonths(1))
                .deadlineDate(LocalDate.now().plusWeeks(3))
                .format("LEAGUE") // 🛑 형식 추가
                .build());


        // 🛑 3. Match 테이블에 테스트 데이터 추가

        // 3-1. 토너먼트 매치 데이터 (결과 입력이 필요한 상태)
        matchRepository.save(Match.builder()
                .contestId(contestTournament.getId()) // 1번 대회 참조
                .round("16강 A조")
                .matchTime(LocalDate.now().plusDays(11).toString() + " 10:00") // 날짜 생성
                .teamA("Team A (전진)")
                .teamB("Team B (돌격)")
                .scoreA(null)
                .scoreB(null)
                .status("UPCOMING")
                .build());

        // 3-2. 토너먼트 매치 데이터 (이미 결과가 입력된 상태)
        matchRepository.save(Match.builder()
                .contestId(contestTournament.getId())
                .round("16강 B조")
                .matchTime(LocalDate.now().plusDays(11).toString() + " 11:00")
                .teamA("Team C (불사조)")
                .teamB("Team D (독수리)")
                .scoreA(3)
                .scoreB(1)
                .status("FINISHED") // 완료 상태
                .build());

        // 3-3. 리그 매치 데이터 (결과 입력이 필요한 상태)
        matchRepository.save(Match.builder()
                .contestId(contestLeague.getId()) // 2번 대회 참조
                .round("리그 1차전")
                .matchTime(LocalDate.now().plusMonths(1).toString() + " 13:00")
                .teamA("Phoenix")
                .teamB("Dragon")
                .scoreA(null)
                .scoreB(null)
                .status("UPCOMING")
                .build());

    }
}

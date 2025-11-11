//이병철
package com.project.nextmatch; // 또는 com.project.nextmatch

import com.project.nextmatch.domain.Contest;
import com.project.nextmatch.domain.Member;
import com.project.nextmatch.repository.ContestRepository;
import com.project.nextmatch.repository.MemberRepository;
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
    // private final PasswordEncoder passwordEncoder; // 암호화가 있다면 주석 해제

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // 1. 테스트 회원 생성 및 저장
        Member testMember = Member.builder()
                .username("testuser")
                // .password(passwordEncoder.encode("password123")) // 암호화가 있다면 사용
                .password("password123")
                .build();

        memberRepository.save(testMember);

        // 2. 테스트 대회 생성 및 저장 (Event Entity의 모든 필드 포함)
        contestRepository.save(Contest.builder()
                .member(testMember)
                .title("제1회 NextMatch 축구대회") // ⭐ title 필수!
                .eventCategory("축구")
                .status("ONGOING")
                .imageUrl("https://example.com/image1.jpg") // 실제 이미지 URL로 변경
                .description("누구나 참가 가능한 미니 축구 토너먼트입니다.")
                .startDate(LocalDate.now().plusDays(10))
                .deadlineDate(LocalDate.now().plusDays(5))
                .build());

        contestRepository.save(Contest.builder()
                .member(testMember)
                .title("2025 배드민턴 동호회 리그")
                .eventCategory("배드민턴")
                .status("UPCOMING")
                .imageUrl("https://example.com/image2.jpg")
                .description("경쟁적인 리그전입니다.")
                .startDate(LocalDate.now().plusMonths(1))
                .deadlineDate(LocalDate.now().plusWeeks(3))
                .build());
    }
}
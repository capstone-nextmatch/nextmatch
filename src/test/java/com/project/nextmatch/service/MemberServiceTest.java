/**
 * Filename: MemberServiceTest.java
 * Author: Sejun Park
 */
package com.project.nextmatch.service;

import com.project.nextmatch.domain.Member;
import com.project.nextmatch.dto.MemberUpdateRequestDto;
import com.project.nextmatch.repository.ContestRepository;
import com.project.nextmatch.repository.ContestRecordRepository;
import com.project.nextmatch.repository.MatchResultRepository;
import com.project.nextmatch.repository.MemberRepository;
import com.project.nextmatch.repository.WishListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    // --------------------------------------------------------
    // 💡 컨텍스트 로딩 오류 해결을 위한 MockBean 선언 (핵심 수정 사항)
    // MemberServiceTest에 불필요한 다른 서비스들이 의존하는 Repository들을 Mocking
    // --------------------------------------------------------
    @MockBean
    private MatchResultRepository matchResultRepository; // MatchService 의존성 Mocking

    @MockBean
    private ContestRecordRepository contestRecordRepository; // AwardService/EliminationService 의존성 Mocking

    // 혹시 PageController가 ContestRepository, WishListRepository 등을 직접 의존한다면 추가:
    @MockBean
    private ContestRepository contestRepository;

    @MockBean
    private WishListRepository wishListRepository;

    // --------------------------------------------------------

    private Member testMember;
    private final Long NON_EXISTENT_ID = 9999L;

    @BeforeEach
    void setUp() {
        // 테스트에 사용할 사용자 데이터 생성 (Member 엔티티 구조에 맞춰 수정이 필요합니다.)
        testMember = Member.builder()
                .username("userTest")
                .password("testPass")
                // .email("test@test.com") // 필드 존재 시 주석 해제
                .build();
        testMember = memberRepository.save(testMember);
    }

    // --- 4.0 내 정보 수정 폼 확인 (성공 케이스) ---
    @Test
    @DisplayName("4.0_현재_정보_조회_시_MemberUpdateRequestDto로_정확히_반환한다")
    void getMemberInfoForUpdate_Success() {
        // given: setUp에서 저장된 testMember의 ID를 사용합니다.
        Long memberId = testMember.getId();

        // when: Service 메서드를 호출하여 사용자 정보를 조회합니다.
        MemberUpdateRequestDto dto = memberService.getMemberInfoForUpdate(memberId);

        // then 1: 반환된 DTO가 null이 아니어야 합니다.
        assertThat(dto).isNotNull();

        // then 2: DTO의 필드들이 DB의 데이터와 정확히 일치해야 합니다.
        assertThat(dto.getId()).isEqualTo(memberId);
        assertThat(dto.getUsername()).isEqualTo("userTest");
        // assertThat(dto.getEmail()).isEqualTo("test@test.com"); // Email 필드 검증 (필드 존재 시)
    }

    // --- 4.1 예외 처리 (존재하지 않는 ID 조회) ---
    @Test
    @DisplayName("4.1_존재하지_않는_ID_조회_시_IllegalArgumentException을_던진다")
    void getMemberInfoForUpdate_NotFound() {
        // when / then: 존재하지 않는 ID로 조회 시 예외가 발생해야 합니다.
        assertThrows(IllegalArgumentException.class, () -> {
            memberService.getMemberInfoForUpdate(NON_EXISTENT_ID);
        });
    }
}
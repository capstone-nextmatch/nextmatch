/**
 * Filename: WishlistServiceTest.java
 * Author: Sejun Park
 */
package com.project.nextmatch.service;

import com.project.nextmatch.domain.Contest;
import com.project.nextmatch.domain.Member;
import com.project.nextmatch.domain.WishList;
import com.project.nextmatch.repository.ContestRepository;
import com.project.nextmatch.repository.MemberRepository;
import com.project.nextmatch.repository.WishListRepository;
// 💡 컨텍스트 로딩 오류 해결을 위해 필요한 Repository 임포트
import com.project.nextmatch.repository.MatchResultRepository;
import com.project.nextmatch.repository.ContestRecordRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class WishListServiceTest {

    @Autowired
    private WishListService wishListService;
    @Autowired
    private WishListRepository wishListRepository;
    @Autowired
    private ContestRepository contestRepository;
    @Autowired
    private MemberRepository memberRepository;

    // --------------------------------------------------------
    // 💡 컨텍스트 로딩 오류 해결을 위한 MockBean 선언 (핵심 수정 사항)
    // ApplicationContext에 Bean이 없어서 실패하는 Repository/Service를 Mocking 합니다.
    // --------------------------------------------------------

    // AwardService, EliminationService 등이 의존하는 Repository Mocking
    @MockBean
    private MatchResultRepository matchResultRepository;

    @MockBean
    private ContestRecordRepository contestRecordRepository;

    @MockBean
    private MatchService matchService;

    @MockBean
    private AwardService awardService;

    @MockBean
    private EliminationService eliminationService;

    @MockBean
    private RoundService roundService;

    private Member memberA;
    private Member memberB;
    private Contest contestX;

    @BeforeEach
    void setUp() {
        // 1. 테스트 사용자 생성 및 저장 (🌟 수정: Member.builder() 사용)
        memberA = memberRepository.save(
                Member.builder()
                        .username("userA")
                        .password("passA")
                        .build()
        );
        memberB = memberRepository.save(
                Member.builder()
                        .username("userB")
                        .password("passB")
                        .build()
        );

        // 2. Contest 객체를 생성 및 저장합니다.
        Contest newContest = new Contest("대회 X", 0);
        // NOTE: Contest 엔티티의 필드가 @Builder가 아닌 setMember를 통해 설정되어야 합니다.
        newContest.setMember(memberA);

        contestX = contestRepository.save(newContest);
    }

    // --- 1.2 좋아요 등록 및 누적 기능 테스트 ---
    @Test
    @DisplayName("1.2_좋아요_등록과_누적_시_WishList_생성_및_좋아요_수_증가")
    void registerWishListAndIncrementCount() {
        // when 1: 사용자 A가 좋아요 등록
        wishListService.toggleWishList(contestX.getId(), memberA.getId());

        // then 1-1: 좋아요 수가 1인지 확인
        Contest updatedContest = contestRepository.findById(contestX.getId()).orElseThrow();
        assertThat(updatedContest.getLikeCount()).isEqualTo(1);

        // then 1-2: WishList 엔티티가 존재하는지 확인
        Optional<WishList> optionalA = wishListRepository.findByMember_IdAndContest_Id(memberA.getId(), contestX.getId());
        assertThat(optionalA.isPresent()).isTrue();

        // when 2: 사용자 B가 좋아요 등록 (누적)
        wishListService.toggleWishList(contestX.getId(), memberB.getId());

        // then 2: 좋아요 수가 2로 누적되었는지 확인
        Contest finalContest = contestRepository.findById(contestX.getId()).orElseThrow();
        assertThat(finalContest.getLikeCount()).isEqualTo(2);
    }

    // --- 1.1 좋아요 취소 기능 테스트 ---
    @Test
    @DisplayName("1.1_좋아요_취소_시_WishList_삭제_및_좋아요_수_감소")
    void cancelWishListAndDecrementCount() {
        // given: 사용자 A가 이미 대회 X에 좋아요를 누른 상태
        wishListRepository.save(new WishList(memberA, contestX));
        contestX.incrementLikeCount();
        contestRepository.save(contestX);

        // when: 사용자 A가 다시 좋아요 토글을 시도 (취소 동작)
        boolean isLiked = wishListService.toggleWishList(contestX.getId(), memberA.getId());

        // then 1: 반환 값이 false (좋아요 취소)인지 확인
        assertThat(isLiked).isFalse();

        // then 2: 좋아요 수 감소 확인
        Contest updatedContest = contestRepository.findById(contestX.getId()).orElseThrow();
        assertThat(updatedContest.getLikeCount()).isEqualTo(0);

        // then 3: WishList 엔티티 삭제 확인
        Optional<WishList> optionalA = wishListRepository.findByMember_IdAndContest_Id(memberA.getId(), contestX.getId());
        assertThat(optionalA.isEmpty()).isTrue();
    }

    // --- 5.0 유효성 검사 (중복 요청 시 좋아요 취소) 테스트 ---
    @Test
    @DisplayName("5.0_이미_좋아요_한_대회에_재요청시_좋아요가_취소(삭제)된다")
    void toggleWishList_ShouldRemoveExisting() {
        // given: memberA가 contestX에 좋아요가 등록되어 있는 상태
        Long memberId = memberA.getId();
        Long contestId = contestX.getId();

        // 1. 좋아요를 먼저 등록합니다. (toggleService 사용)
        wishListService.toggleWishList(contestId, memberId);

        // 중간 확인: 좋아요가 존재해야 하고 카운트가 1이어야 함
        assertThat(wishListRepository.findByMember_IdAndContest_Id(memberId, contestId)).isPresent();
        assertThat(contestRepository.findById(contestId).get().getLikeCount()).isEqualTo(1);


        // when:
        // 2. 같은 사용자(memberId)가 같은 대회(contestId)에 다시 좋아요 요청(toggle)을 합니다.
        wishListService.toggleWishList(contestId, memberId);


        // then:
        // 3. DB에서 해당 좋아요 데이터가 삭제되었는지 확인합니다.
        assertThat(wishListRepository.findByMember_IdAndContest_Id(memberId, contestId)).isNotPresent();

        // 4. Contest의 좋아요 수도 0으로 감소했는지 확인합니다.
        Contest updatedContest = contestRepository.findById(contestId).get();
        assertThat(updatedContest.getLikeCount()).isEqualTo(0);
    }

    @Test // --- 6.0 저장 대회 목록 최대수 (10개) 제한 테스트 ---
    @DisplayName("6.0_최대_10개_초과_시_IllegalStateException을_던져_등록을_막는다")
    void registerWishList_ShouldThrowExceptionWhenLimitExceeded() {
        // given: memberA가 10개의 대회를 이미 좋아요 한 상태를 만듭니다.
        Long memberId = memberA.getId();

        // 1. 반복문을 사용하여 10개의 가짜 대회와 좋아요 기록을 DB에 저장합니다.
        for (int i = 1; i <= 10; i++) {
            // 매번 새로운 대회를 생성
            Contest contest = new Contest("LimitTest-대회-" + i, 0);

            // 🌟 수정: Contest 엔티티에 memberA를 설정합니다. (Contest.setMember(memberA)를 통해 관계 설정)
            contest.setMember(memberA);

            // DB에 저장
            contest = contestRepository.save(contest);

            // 해당 대회에 memberA가 좋아요를 누른 WishList를 DB에 직접 저장
            wishListRepository.save(new WishList(memberA, contest));
        }

        // 2. 현재 좋아요 개수가 정확히 10개인지 확인합니다. (countByMember_Id 메서드 검증)
        assertThat(wishListRepository.countByMember_Id(memberId)).isEqualTo(10);

        // 3. 11번째로 등록할 새로운 대회를 준비합니다.
        Contest contest11ToSave = new Contest("LimitTest-대회-11", 0);
        contest11ToSave.setMember(memberA);

        // DB에 저장하고, 영속화된 Contest 객체를 받습니다.
        final Contest contest11 = contestRepository.save(contest11ToSave);

        // 🌟 수정: 람다에서 사용할 ID만 따로 final 변수에 저장합니다.
        final Long contest11Id = contest11.getId();


        // when / then:
        // 4. 11번째 대회에 좋아요를 등록하려고 시도하면, 예외가 발생해야 합니다.
        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class, () -> {
            // 11번째 등록 시도 - final 변수를 사용합니다.
            wishListService.toggleWishList(contest11Id, memberId);
        }, "최대 저장 가능 수를 초과했을 때 예외가 발생해야 합니다.");

        // 5. 예외 발생 후에도 DB에 등록된 좋아요 수는 10개로 유지되어야 합니다.
        assertThat(wishListRepository.countByMember_Id(memberId)).isEqualTo(10);

        // 6. 11번째 대회의 좋아요 수가 0으로 유지되어야 합니다.
        Contest finalContest11 = contestRepository.findById(contest11Id).get();
        assertThat(finalContest11.getLikeCount()).isEqualTo(0);
    }
}
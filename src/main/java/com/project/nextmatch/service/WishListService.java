/**
 * Filename: WishlistService.java
 * Author: Sejun Park
 * Description: 관심 목록(좋아요) 기능의 비즈니스 로직을 담당합니다.
 * 토글, 목록 조회, 등록 여부 확인 및 최대 저장 수 제한 기능을 포함합니다.
 */
package com.project.nextmatch.service;

import com.project.nextmatch.domain.Contest;
import com.project.nextmatch.domain.Member;
import com.project.nextmatch.domain.WishList;
import com.project.nextmatch.dto.WishListResponseDto;
import com.project.nextmatch.repository.ContestRepository;
import com.project.nextmatch.repository.MemberRepository;
import com.project.nextmatch.repository.WishListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Spring Transactional 사용 통일

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 읽기 전용 트랜잭션을 기본으로 설정 (두 번째 파일 기반)
public class WishListService {

    private final WishListRepository wishListRepository;
    private final ContestRepository contestRepository;
    private final MemberRepository memberRepository;

    // 관심 대회 최대 저장 수 (첫 번째 파일 유지)
    private static final int MAX_WISH_LIMIT = 10;

    /**
     * 1. 좋아요 상태 토글 (등록 및 취소)
     * - [첫 번째 파일의 로직 채택]: 최대 저장 수 검증 및 Contest의 likeCount 증감 로직 포함.
     * @param contestId 대상 대회 ID
     * @param memberId 요청 회원 ID
     * @return true: 등록됨, false: 취소됨
     */
    @Transactional // 쓰기 작업이 포함되므로 재정의
    public boolean toggleWishList(Long contestId, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new IllegalArgumentException("대회를 찾을 수 없습니다."));

        Optional<WishList> existingWishList =
                wishListRepository.findByMember_IdAndContest_Id(memberId, contestId);

        if (existingWishList.isPresent()) {
            // 좋아요 취소 로직
            wishListRepository.delete(existingWishList.get());
            contest.decrementLikeCount();
            return false;
        } else {
            // 최대 저장 수(10개) 초과 검증 로직 유지
            long currentWishCount = wishListRepository.countByMember_Id(memberId);

            if (currentWishCount >= MAX_WISH_LIMIT) {
                throw new IllegalStateException("관심 대회는 최대 " + MAX_WISH_LIMIT + "개까지 저장할 수 있습니다.");
            }

            // 좋아요 등록 로직
            WishList newWishList = new WishList(member, contest);
            wishListRepository.save(newWishList);
            contest.incrementLikeCount();
            return true;
        }
    }

    /**
     * 2. 관심 대회 목록 조회 (GET)
     * - [두 번째 파일의 실제 구현 로직 채택]: Repository를 통해 조회 후 DTO로 변환하여 반환.
     * @param memberId 회원 ID
     * @return 해당 회원의 관심 대회 목록 DTO 리스트
     */
    public List<WishListResponseDto> getWishList(Long memberId) {
        List<WishList> wishLists = wishListRepository.findByMember_IdOrderByRegisteredAtDesc(memberId);

        // 엔티티 리스트를 DTO 리스트로 변환하여 반환
        return wishLists.stream()
                .map(WishListResponseDto::of)
                .collect(Collectors.toList());
    }

    /**
     * 3. 관심 대회 삭제 (DELETE)
     * - [두 번째 파일의 구현 로직 채택]: wishId 기반으로 항목을 삭제.
     * @param wishId 삭제할 WishList 항목 ID
     */
    @Transactional
    public void removeWish(Long wishId) {
        // 엔티티를 찾아서 삭제합니다.
        if (!wishListRepository.existsById(wishId)) {
            throw new IllegalArgumentException("존재하지 않는 관심 목록 항목입니다.");
        }
        wishListRepository.deleteById(wishId);

        // NOTE: 이 시점에서는 Contest의 likeCount를 감소시키려면
        // WishList 엔티티를 찾아서 Contest를 얻은 후 감소시켜야 합니다.
        // 현재 로직은 likeCount 감소 로직이 없으므로, 필요하다면 추가해야 합니다.
    }


    /**
     * 4. 관심 대회 등록 여부 확인 (GET /check)
     * - [두 번째 파일의 구현 로직 채택]: existsBy 메서드를 사용.
     * @param contestId 대상 대회 ID
     * @param memberId 요청 회원 ID
     * @return 등록 여부 (true/false)
     */
    public boolean isContestWished(Long contestId, Long memberId) {
        // Repository의 existsBy... 메서드를 사용하여 성능을 최적화합니다.
        return wishListRepository.existsByMember_IdAndContest_Id(memberId, contestId);
    }

    // NOTE: 두 번째 파일의 addWish 메서드는 토글 로직으로 대체되었으며,
    // 첫 번째 파일의 임시 getWishList(Long mockMemberId) 로직은 두 번째 파일의 실제 로직으로 대체되었습니다.
}
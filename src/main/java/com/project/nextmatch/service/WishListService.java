package work.wish.wishlist1.service;

import work.wish.wishlist1.dto.WishListRequestDto;
import work.wish.wishlist1.dto.WishListResponseDto;
import work.wish.wishlist1.domain.Contest;
import work.wish.wishlist1.domain.Member;
import work.wish.wishlist1.domain.WishList;
import work.wish.wishlist1.repository.ContestRepository;
import work.wish.wishlist1.repository.MemberRepository;
import work.wish.wishlist1.repository.WishListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishListService {

    private final WishListRepository wishListRepository;
    // 실제 프로젝트를 위해 Member/Contest Repository가 존재한다고 가정합니다.
    private final MemberRepository memberRepository;
    private final ContestRepository contestRepository;

    /**
     * 관심 대회 추가 (POST)
     */
    @Transactional
    public WishListResponseDto addWish(WishListRequestDto requestDto) {
        // 1. 회원과 대회 엔티티 조회 (실제 DB에서 찾아와야 함)
        Member member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Contest contest = contestRepository.findById(requestDto.getContestId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대회입니다."));

        // 2. 이미 등록되어 있는지 확인 (중복 등록 방지)
        if (wishListRepository.existsByMember_IdAndContest_Id(member.getId(), contest.getId())) {
            // 이 경우, 보통 409 Conflict 에러로 프론트에 응답합니다.
            throw new IllegalStateException("이미 관심 목록에 등록된 대회입니다.");
        }

        // 3. WishList 엔티티 생성 및 저장
        WishList wishList = WishList.builder()
                .member(member)
                .contest(contest)
                .build();

        WishList savedWish = wishListRepository.save(wishList);

        return WishListResponseDto.of(savedWish);
    }

    /**
     * 관심 대회 목록 조회 (GET)
     */
    public List<WishListResponseDto> getWishList(Long memberId) {
        List<WishList> wishLists = wishListRepository.findByMember_IdOrderByRegisteredAtDesc(memberId);

        // 엔티티 리스트를 DTO 리스트로 변환하여 반환
        return wishLists.stream()
                .map(WishListResponseDto::of)
                .collect(Collectors.toList());
    }

    /**
     * 관심 대회 삭제 (DELETE)
     */
    @Transactional
    public void removeWish(Long wishId) {
        // 엔티티를 찾아서 삭제합니다.
        if (!wishListRepository.existsById(wishId)) {
            throw new IllegalArgumentException("존재하지 않는 관심 목록 항목입니다.");
        }
        wishListRepository.deleteById(wishId);
    }

    /**
     * 관심 대회 등록 여부 확인 (GET /check)
     */
    public boolean isContestWished(Long memberId, Long contestId) {
        return wishListRepository.existsByMember_IdAndContest_Id(memberId, contestId);
    }
}

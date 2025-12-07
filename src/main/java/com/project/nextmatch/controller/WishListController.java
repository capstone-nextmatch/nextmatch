/**
 * Filename: WishlistController.java
 * Author: Sejun Park
 * Description: 관심 목록(좋아요) 기능을 RESTful 토글 방식으로 통합하고,
 * 기존의 목록 조회, 삭제, 등록 여부 확인 엔드포인트를 모두 포함합니다.
 */
package com.project.nextmatch.controller;

import com.project.nextmatch.dto.WishListRequestDto;
import com.project.nextmatch.dto.WishListResponseDto;
import com.project.nextmatch.service.WishListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist") // 기본 경로 설정
@RequiredArgsConstructor
public class WishListController {

    private final WishListService wishListService;

    /**
     * 1. 좋아요 상태 토글 (등록 및 취소) (POST /api/wishlist)
     * - [토글 방식 채택] POST 요청 하나로 등록(Add)과 취소(Remove)를 모두 처리합니다.
     * - 요청: { contestId }
     * - 응답: 토글 후 상태 (true: 등록, false: 취소) (HTTP 200 OK)
     */
    @PostMapping
    public ResponseEntity<Boolean> toggleWish(@RequestBody WishListRequestDto requestDto) {
        // ✨ [수정] 로그인한 사용자 ID를 임시로 1L로 설정 (보안 적용 전 임시 처리)
        Long currentMemberId = 1L;

        // Service에서 등록/취소 로직을 수행하고 최종 상태(등록 여부)를 반환 받습니다.
        boolean isLiked = wishListService.toggleWishList(requestDto.getContestId(), currentMemberId);

        // 등록/취소에 관계없이 200 OK를 반환하며, Body에는 최종 상태를 전달합니다.
        return ResponseEntity.ok(isLiked);
    }

    /**
     * 2. 관심 대회 목록 조회 (GET /api/wishlist/{memberId})
     * - 요청: Path Variable로 회원 ID 수신
     * - 응답: 해당 회원의 관심 대회 목록 (HTTP 200 OK)
     */
    @GetMapping("/{memberId}")
    public ResponseEntity<List<WishListResponseDto>> getWishList(@PathVariable Long memberId) {
        // 임시 Object 대신 실제 DTO를 반환하도록 수정 (두 번째 파일 기반)
        List<WishListResponseDto> wishList = wishListService.getWishList(memberId);
        return ResponseEntity.ok(wishList);
    }

    /**
     * 3. 관심 대회 삭제 (DELETE /api/wishlist/{contestId} 또는 {wishId})
     * - [RESTful 원칙에 맞춰 /api/wishlist/{wishId}를 사용하되, 토글 기능의 구현을 위해
     * 토글 서비스 메서드를 활용하여 memberId와 contestId 기반으로 삭제합니다.]
     * - 요청: 삭제할 WishList ID 수신 (두 번째 파일의 RESTful 방식)
     * - 응답: 본문 없음 (HTTP 204 No Content)
     */
    @DeleteMapping("/{wishId}")
    public ResponseEntity<Void> removeWish(@PathVariable Long wishId) {
        // 첫 번째 파일의 토글 기능을 활용하지 않고, 두 번째 파일의 RESTful 삭제 방식을 따릅니다.
        // 클라이언트가 WishList 자체의 ID를 보내는 것이 RESTful 원칙에 더 부합합니다.
        wishListService.removeWish(wishId);

        // 삭제 성공 후 204 No Content 상태 코드를 반환합니다.
        return ResponseEntity.noContent().build();
    }

    // NOTE: 만약 {contestId} 기반 삭제가 필요하다면 아래 메서드를 추가할 수 있습니다.
    /*
    @DeleteMapping("/contest/{contestId}")
    public ResponseEntity<Void> removeWishByContestId(@PathVariable Long contestId) {
        Long currentMemberId = 1L; // Security 적용 전 임시 ID
        wishListService.removeWishByContestIdAndMemberId(contestId, currentMemberId);
        return ResponseEntity.noContent().build();
    }
    */


    /**
     * 4. 관심 대회 등록 여부 확인 (GET /api/wishlist/check?memberId=...&contestId=...)
     * - 요청: Query Parameter로 memberId와 contestId 수신
     * - 응답: 등록 여부 (true/false) (HTTP 200 OK)
     */
    @GetMapping("/check")
    public ResponseEntity<Boolean> isContestWished(
            @RequestParam Long memberId,
            @RequestParam Long contestId) {

        // Service의 isContestWished(memberId, contestId) 메서드를 호출합니다.
        boolean isWished = wishListService.isContestWished(memberId, contestId);
        return ResponseEntity.ok(isWished);
    }
}
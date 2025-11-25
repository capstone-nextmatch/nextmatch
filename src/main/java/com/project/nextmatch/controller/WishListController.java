/**
 * Filename: WishlistController.java
 * Author: Sejun Park
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
@RequestMapping("/api/wishlist") // 기본 경로 설정 (REST API의 표준)
@RequiredArgsConstructor
public class WishListController {

    private final WishListService wishListService;

    /**
     * 1. 관심 대회 추가 (POST /api/wishlist)
     * - 요청: { memberId, contestId }
     * - 응답: 저장된 WishList 항목 정보 (HTTP 201 Created)
     */
    @PostMapping
    public ResponseEntity<WishListResponseDto> addWish(@RequestBody WishListRequestDto requestDto) {
        // Service로 DTO를 전달하여 비즈니스 로직(저장, 중복 확인 등)을 수행합니다.
        WishListResponseDto response = wishListService.addWish(requestDto);
        // 새로운 리소스를 생성했을 때는 201 Created 상태 코드를 반환하는 것이 RESTful 원칙입니다.
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 2. 관심 대회 목록 조회 (GET /api/wishlist/{memberId})
     * - 요청: Path Variable로 회원 ID 수신
     * - 응답: 해당 회원의 관심 대회 목록 (HTTP 200 OK)
     */
    @GetMapping("/{memberId}")
    public ResponseEntity<List<WishListResponseDto>> getWishList(@PathVariable Long memberId) {
        List<WishListResponseDto> wishList = wishListService.getWishList(memberId);
        return ResponseEntity.ok(wishList); // 200 OK
    }

    /**
     * 3. 관심 대회 삭제 (DELETE /api/wishlist/{wishId})
     * - 요청: Path Variable로 삭제할 WishList ID 수신
     * - 응답: 본문 없음 (HTTP 204 No Content)
     */
    @DeleteMapping("/{wishId}")
    public ResponseEntity<Void> removeWish(@PathVariable Long wishId) {
        wishListService.removeWish(wishId);
        // 삭제 성공 후에는 204 No Content 상태 코드를 반환하는 것이 RESTful 원칙입니다.
        return ResponseEntity.noContent().build();
    }

    /**
     * 4. 관심 대회 등록 여부 확인 (GET /api/wishlist/check?memberId=...&contestId=...)
     * - 요청: Query Parameter로 memberId와 contestId 수신
     * - 응답: 등록 여부 (true/false) (HTTP 200 OK)
     */
    @GetMapping("/check")
    public ResponseEntity<Boolean> isContestWished(
            @RequestParam Long memberId,
            @RequestParam Long contestId) {

        boolean isWished = wishListService.isContestWished(memberId, contestId);
        return ResponseEntity.ok(isWished);
    }

}

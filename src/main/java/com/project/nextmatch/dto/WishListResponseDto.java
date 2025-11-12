package com.project.nextmatch.dto;

import com.project.nextmatch.domain.WishList;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class WishListResponseDto {
    private Long wishId;
    private Long contestId;
    private String contestTitle; // 대회명
    private LocalDateTime registeredAt;

    // WishList 엔티티를 DTO로 변환하는 팩토리 메서드
    public static WishListResponseDto of(WishList wishList) {
        return WishListResponseDto.builder()
                .wishId(wishList.getId())
                .contestId(wishList.getContest().getId())
                .contestTitle(wishList.getContest().getTitle())
                .registeredAt(wishList.getRegisteredAt())
                .build();
    }
}
/**
 * Filename: WishlistController.java
 * Author: Sejun Park
 */
package com.project.nextmatch.controller;

import com.project.nextmatch.dto.WishListRequestDto;
import com.project.nextmatch.service.WishListService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong; // ✨ Long 타입 인자를 모킹하기 위해 추가
import static org.mockito.Mockito.when; // doNothing 대신 when을 사용해 반환값을 모킹할 수 있습니다.
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WishListController.class)
class WishListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WishListService wishListService;

    // 📌 테스트 케이스 3: 좋아요 등록/취소 (Controller)
    @Test
    @DisplayName("3.0_유저가_좋아요_버튼을_클릭하면_Service를_호출하고_200_OK를_반환한다")
    void toggleWishList_ShouldReturnOk() throws Exception {

        // given 1: Service의 toggleWishList 메서드가 호출되면 true (좋아요 등록 성공)를 반환하도록 모킹
        // toggleWishList(Long contestId, Long memberId) 메서드 시그니처에 맞게 인자 2개를 지정합니다.
        when(wishListService.toggleWishList(anyLong(), anyLong())).thenReturn(true);

        // given 2: 요청 본문 JSON
        // 실제 API는 @RequestBody WishListRequestDto를 받지만, Service 호출은 ID를 받습니다.
        String requestJson = "{\"contestId\": 101}"; // memberId는 Controller에서 처리한다고 가정하고 contestId만 포함

        // when: /api/wishlist 경로로 POST 요청 (Controller의 @PostMapping 매핑에 따름)
        mockMvc.perform(post("/api/wishlist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))

                // then: HTTP 상태 코드가 200 OK 인지 확인합니다.
                .andExpect(status().isOk());
    }
}
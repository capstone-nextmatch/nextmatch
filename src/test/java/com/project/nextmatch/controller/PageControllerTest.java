/**
 * Filename: PageController.java
 * Author: Sejun Park
 */
package com.project.nextmatch.controller;

import com.project.nextmatch.service.AwardService;
import com.project.nextmatch.service.EliminationService;
import com.project.nextmatch.service.MatchService;
import com.project.nextmatch.service.WishListService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean; // MockBean import
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

// ✨ PageController만 테스트하도록 컨텍스트를 로드합니다.
@WebMvcTest(PageController.class)
class PageControllerTest {

    @Autowired
    private MockMvc mockMvc; // HTTP 요청을 모의(Mock)하는 핵심 객체

    // 💡 필수 수정: PageController가 의존하는 모든 Service Bean을 Mocking해야 합니다.
    @MockBean
    private WishListService wishListService;

    @MockBean
    private MatchService matchService; // 💡 추가: MatchService Mocking

    @MockBean
    private AwardService awardService; // 💡 추가: AwardService Mocking

    @MockBean
    private EliminationService eliminationService; // 💡 추가: EliminationService Mocking

    // Security 미적용 환경이므로 페이지 로드 성공 여부만 확인합니다.
    @Test
    @DisplayName("2.0_로그인_상태에서_마이페이지_접근_시_성공적으로_mypage_뷰를_반환한다")
    void accessMypageSuccessfully() throws Exception {

        // when: /mypage 경로로 GET 요청을 시도합니다.
        mockMvc.perform(get("/mypage"))

                // then 1: HTTP 상태 코드가 200 OK 인지 확인합니다.
                .andExpect(status().isOk())

                // then 2: 반환되는 뷰 이름이 'mypage'인지 확인합니다.
                .andExpect(view().name("mypage"));
    }
}
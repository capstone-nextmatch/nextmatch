//박세준

package com.project.nextmatch.controller;

import com.project.nextmatch.dto.MatchHistoryDto;
import com.project.nextmatch.dto.WishListResponseDto;
import com.project.nextmatch.service.MatchService;
import com.project.nextmatch.service.WishListService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping; // @RequestMapping("/dashboard") 때문에 import 했으나, 없어도 무방
// 두 번째 코드에 있던 @RequestMapping("/dashboard")는
// 실제로는 클래스 레벨이 아닌 메서드 레벨에서 처리됨

import java.util.List;

@Controller
@RequiredArgsConstructor // WishListService, MatchService 주입을 위해 Lombok 사용
public class PageController {

    private final WishListService wishListService;
    private final MatchService matchService;

    @GetMapping("/main")
    public String main() {
        return "main";
    }

    @GetMapping("/register-contest")
    public String registerContest() {
        return "register-contest";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @GetMapping("/mypage")
    public String mypage(Model model) {
        // TODO: 실제로는 세션/인증 메커니즘을 통해 로그인된 사용자 ID를 가져와야 합니다.
        // 현재는 구현을 위해 임시로 회원 ID 1L을 사용한다고 가정합니다.
        Long loggedInMemberId = 1L;

        // 1. 관심 목록 데이터 조회
        List<WishListResponseDto> wishLists = wishListService.getWishList(loggedInMemberId);
        model.addAttribute("wishLists", wishLists);

        // 2. 전적 및 승률 데이터 조회
        MatchHistoryDto matchHistory = matchService.getMatchHistoryAndWinRate(loggedInMemberId);
        model.addAttribute("matchHistory", matchHistory); // 뷰(HTML)로 전달

        return "mypage";
    }

    @GetMapping("/dashboard") // 두 번째 코드에 있던 엔드포인트 추가
    public String dashboard() {
        return "dashboard";
    }
}
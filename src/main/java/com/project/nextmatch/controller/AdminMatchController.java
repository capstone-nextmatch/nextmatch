//이병철
package com.project.nextmatch.controller;

import com.project.nextmatch.dto.MatchResultRequest;
import com.project.nextmatch.service.AdminMatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/match")
@RequiredArgsConstructor
public class AdminMatchController {

    private final AdminMatchService adminMatchService;

    // 1. 경기 목록/관리 페이지 View 렌더링
    @GetMapping("/list")
    public String showAdminMatchList() {
        return "admin-match-list"; // admin-match-list.html 렌더링
    }

    // 2. 특정 경기의 결과를 입력받는 API
    // POST /admin/match/{matchId}/result
    @PostMapping("/{matchId}/result")
    @ResponseBody
    public String inputMatchResult(@PathVariable Long matchId,
                                   @RequestBody MatchResultRequest request) {

        // Service 호출
        adminMatchService.recordMatchResult(matchId, request);

        return "SUCCESS";
    }
    @GetMapping("/result")
    public String showMatchResultInput(@RequestParam Long contestId) {
        // 실제로는 Model에 contestId를 넣어 상세 정보를 가져오게 해야 하지만,
        // 현재는 contestId를 프론트엔드에 전달하기 위해 @RequestParam만 받습니다.
        return "admin-match-result"; // admin-match-result.html 렌더링
    }
}

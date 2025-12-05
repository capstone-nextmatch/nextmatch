// 이병철
package com.project.nextmatch.controller;

import com.project.nextmatch.domain.Contest; // Contest 엔티티 import
import com.project.nextmatch.dto.ContestListResponse;
import com.project.nextmatch.service.ContestListService;
import com.project.nextmatch.service.ContestService; // ContestService import
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Model import
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;

@Controller
@RequestMapping("/api/event")
@RequiredArgsConstructor
public class ContestListController {

    private final ContestListService contestListService;
    private final ContestService contestService; // ContestService 의존성 주입


    @GetMapping("/list")
    @ResponseBody
    public ResponseEntity<List<ContestListResponse>> listEvents(
            @RequestParam(value = "search", required = false) String search) {

        List<ContestListResponse> events = contestListService.listEvents(search);

        return ResponseEntity.ok(events);
    }

    @GetMapping("/tournaments/detail")
    public String showContestDetail(@RequestParam("id") Long id, Model model) { // Model 객체 추가

        try {
            // 1. ContestService를 사용하여 ID로 상세 정보 조회
            Contest contest = contestService.findContestById(id);

            // 2. 조회된 Contest 객체를 Model에 추가하여 뷰(View)로 전달
            model.addAttribute("contest", contest);

            // 3. 뷰 이름 반환
            return "tournament-detail";

        } catch (IllegalArgumentException e) {
            return "redirect:/api/event/list";
        }
    }
}
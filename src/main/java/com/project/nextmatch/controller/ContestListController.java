// 이병철
package com.project.nextmatch.controller;

import com.project.nextmatch.dto.ContestListResponse;
import com.project.nextmatch.service.ContestListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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

    @GetMapping("/list")
    @ResponseBody
    public ResponseEntity<List<ContestListResponse>> listEvents(
            @RequestParam(value = "search", required = false) String search) {

        List<ContestListResponse> events = contestListService.listEvents(search);

        return ResponseEntity.ok(events);
    }

    @GetMapping("/tournaments/detail")
    public String showContestDetail(@RequestParam("id") Long id) {
        // TODO: 상세 정보 조회 로직 추가 필요

        return "tournament-detail";
    }
}
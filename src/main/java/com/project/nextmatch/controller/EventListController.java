//이병철
package com.project.nextmatch.controller;

import com.project.nextmatch.dto.EventListResponse;
import com.project.nextmatch.service.EventListService; // <--- 새로 만든 Service 사용
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/event")
@RequiredArgsConstructor
public class EventListController {

    private final EventListService eventListService;

    // GET /api/event/list (대회 목록 조회 및 검색)
    @GetMapping("/list")
    public ResponseEntity<List<EventListResponse>> listEvents(
            @RequestParam(value = "search", required = false) String search) {

        List<EventListResponse> events = eventListService.listEvents(search);

        return ResponseEntity.ok(events);
    }
}
package com.project.nextmatch.controller;

import com.project.nextmatch.service.EventService;
import com.project.nextmatch.service.EventService.EventListResponse;
import com.project.nextmatch.dto.EventCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/event")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // POST /api/event (대회 생성)
    @PostMapping
    public ResponseEntity<Long> createEvent(@RequestBody EventCreateRequest request) {
        Long eventId = eventService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventId);
    }

    // GET /api/event/list (대회 목록 조회 및 검색)
    // 클라이언트 JS 코드가 이 URL로 요청하며, 'search' 파라미터를 넘깁니다.
    @GetMapping("/list")
    public ResponseEntity<List<EventListResponse>> listEvents(
            @RequestParam(value = "search", required = false) String search) { // 파라미터 이름 통일

        // EventService의 listEvents(String search) 메서드를 호출합니다.
        // 이 메서드는 EventService.java에서 이미 검색 로직이 포함되도록 수정되었습니다.
        List<EventListResponse> events = eventService.listEvents(search);

        return ResponseEntity.ok(events);
    }
}
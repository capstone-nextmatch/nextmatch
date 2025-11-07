//권동혁

package com.project.nextmatch.controller;

import com.project.nextmatch.dto.ContestCreateRequest;
import com.project.nextmatch.service.ContestService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/event")
@AllArgsConstructor
public class ContestController {

    private final ContestService contestService;

    @PostMapping("/create")
    public ResponseEntity<?> createEvent(@Valid @RequestBody ContestCreateRequest request) {
        try {
            contestService.contestCreate(request);
            return ResponseEntity.ok("대회등록이 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body("아이디가 유효하지 않습니다.");
        }
    }
}

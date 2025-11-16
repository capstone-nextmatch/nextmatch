//권동혁

package com.project.nextmatch.controller;

import com.project.nextmatch.dto.ContestCreateRequest;
import com.project.nextmatch.dto.MatchCreateRequest;
import com.project.nextmatch.service.ContestService;
import com.project.nextmatch.service.PlayerService;
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
    private final PlayerService playerService;

    @PostMapping("/create")
    public ResponseEntity<?> createEvent(@Valid @RequestBody ContestCreateRequest request) {
        try {
            contestService.contestCreate(request);
            return ResponseEntity.ok("대회등록이 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body("아이디가 유효하지 않습니다.");
        }
    }

    @PostMapping("/create/matches")
    public ResponseEntity<?> createMatch(@Valid @RequestBody MatchCreateRequest request) {
        try {
            //MemberID -> CreatePlayer
            playerService.registerPlayers(request);
            //Player -> Create Match and Round
            contestService.createAllMatches(request);
            return ResponseEntity.ok("각 경기생성이 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body("아이디가 유효하지 않습니다.");
        }
    }
}

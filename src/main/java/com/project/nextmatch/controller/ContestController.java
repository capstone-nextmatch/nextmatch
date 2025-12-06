//권동혁

package com.project.nextmatch.controller;

import com.project.nextmatch.dto.ContestCreateRequest;
import com.project.nextmatch.dto.MatchCreateRequest; // MatchCreateRequest 추가
import com.project.nextmatch.service.ContestService;
import com.project.nextmatch.service.PlayerService; // PlayerService 추가
import jakarta.persistence.EntityNotFoundException; // 예외 처리 추가
import jakarta.validation.Valid; // @Valid 어노테이션 추가
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus; // HttpStatus 추가
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
    private final PlayerService playerService; // PlayerService 의존성 추가

    @PostMapping("/create")
    public ResponseEntity<?> createEvent(@Valid @RequestBody ContestCreateRequest request) { // @Valid 어노테이션 추가
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

            // 참가자 수 홀수 검증 먼저
            if (request.getMemberId().size() % 2 != 0) {
                throw new IllegalArgumentException("참가자 수가 홀수입니다.");
            }

            //MemberID -> CreatePlayer
            playerService.registerPlayers(request);
            //Player -> Create Match and Round
            contestService.createAllMatches(request);
            return ResponseEntity.ok("각 경기생성이 완료되었습니다.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
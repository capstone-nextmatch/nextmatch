package com.project.nextmatch.controller;

import com.project.nextmatch.dto.MatchResultRequest;
import com.project.nextmatch.service.MatchService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@AllArgsConstructor
public class MatchController {
    private final MatchService matchService;

    @PostMapping("/results")
    public ResponseEntity<String> submitResults(@RequestBody List<MatchResultRequest> results) {
        try {
            matchService.submitMatchResults(results);
            return ResponseEntity.ok("경기 결과가 저장되었습니다.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

}

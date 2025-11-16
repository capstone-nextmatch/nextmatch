package com.project.nextmatch.controller;

import com.project.nextmatch.dto.MatchResultRequest;
import com.project.nextmatch.service.MatchService;
import lombok.AllArgsConstructor;
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
        matchService.submitMatchResults(results);
        return ResponseEntity.ok("경기 결과가 저장되었습니다.");
    }

}

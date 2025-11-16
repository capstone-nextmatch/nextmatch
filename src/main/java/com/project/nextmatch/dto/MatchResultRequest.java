package com.project.nextmatch.dto;

import lombok.Data;

@Data
public class MatchResultRequest {
    private Long matchId;
    private Integer score1;
    private Integer score2;
}

package com.project.nextmatch.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MatchResultRequest {
    @NotNull(message = "matchId는 필수입니다.")
    private Long matchId;

    @NotNull(message = "score1은 필수입니다.")
    private Integer score1;

    @NotNull(message = "score2는 필수입니다.")
    private Integer score2;

}

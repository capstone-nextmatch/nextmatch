//이병철, 권동혁
package com.project.nextmatch.dto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data // Getter, Setter, RequiredArgsConstructor 등을 자동 생성합니다.
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MatchResultRequest {

    // 경기 ID (URL Path Variable로 받지만, RequestBody에 포함될 경우를 위해 정의)
    @NotNull(message = "matchId는 필수입니다.")
    private Long matchId;

    // A팀 점수 (예: 축구 2, 농구 98)
    @NotNull(message = "scoreA은 필수입니다.")
    private Integer scoreA;

    // B팀 점수
    @NotNull(message = "scoreB은 필수입니다.")
    private Integer scoreB;

    // 승리 팀 ID 또는 승리 팀 이름 (프론트엔드에서 승자 선택 시 사용될 수 있음)
    private String winnerTeamName;

    public MatchResultRequest(Integer scoreA, Integer scoreB) {
        this.scoreA = scoreA;
        this.scoreB = scoreB;
        // 나머지 필드는 null로 유지
    }

}


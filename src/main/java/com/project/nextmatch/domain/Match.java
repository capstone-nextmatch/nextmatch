//이병철
package com.project.nextmatch.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "match") // 테이블 이름이 'match'임을 명시
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Contest 테이블의 ID를 참조 (contest_id bigint NN)
    @Column(name = "contest_id", nullable = false)
    private Long contestId;

    // 경기 라운드 (예: Round 1, Quarter Final)
    @Column(name = "round", length = 50, nullable = false)
    private String round;

    // 매치 시간 (match_time datetime NN)
    @Column(name = "match_time", nullable = false)
    private String matchTime;

    // A팀 (team_a varchar(100))
    @Column(name = "team_a", length = 100)
    private String teamA;

    // B팀 (team_b varchar(100))
    @Column(name = "team_b", length = 100)
    private String teamB;

    // A팀 점수 (score_a int)
    @Column(name = "score_a")
    private Integer scoreA;

    // B팀 점수 (score_b int)
    @Column(name = "score_b")
    private Integer scoreB;

    // 경기 상태 (예: UPCOMING, FINISHED)
    @Column(name = "status", length = 20)
    private String status;

    // 💡 토너먼트 진행을 위해 필요한 다음 라운드 매치 ID (Optional)
    // 현재 스키마에는 없지만, 토너먼트 로직을 위해 임시로 추가하거나 설계 변경 필요
    @Setter
    @Column(name = "next_match_id")
    private Long nextMatchId;

    // --- 경기 결과 업데이트 메서드 (Service에서 사용) ---
    public void updateResult(Integer scoreA, Integer scoreB, String status) {
        this.scoreA = scoreA;
        this.scoreB = scoreB;
        this.status = status;
    }
    /**
     * 다음 라운드 매치 엔티티의 팀 슬롯(A 또는 B)을 승자 이름으로 채웁니다.
     * @param teamName 다음 라운드로 진출하는 팀 이름
     * @param isTeamA 업데이트할 슬롯이 Team A 쪽인지 여부 (true: Team A, false: Team B)
     */
    public void updateTeamSlot(String teamName, boolean isTeamA) {
        if (isTeamA) {
            this.teamA = teamName;
        } else {
            this.teamB = teamName;
        }
    }

}

//이병철, 권동혁
package com.project.nextmatch.repository;

import com.project.nextmatch.domain.Match; // Match 엔티티 경로에 맞게 수정

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.project.nextmatch.domain.Round;

import java.util.List;


@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByRound(Round round);

    // 🛑 경기의 점수와 상태를 업데이트하는 쿼리 (핵심 로직 1)
    @Modifying
    @Query("UPDATE Match m SET m.scoreA = :scoreA, m.scoreB = :scoreB, m.status = :status WHERE m.id = :matchId")
    void updateMatchResult(@Param("matchId") Long matchId,
                           @Param("scoreA") Integer scoreA,
                           @Param("scoreB") Integer scoreB,
                           @Param("status") String status);

    // 🛑 다음 라운드 매치에 승자를 반영하는 쿼리 (핵심 로직 2 - 토너먼트용)
    // 다음 라운드 매치 ID와 해당 매치에서 팀이 들어갈 슬롯(team_a 또는 team_b)을 구분해야 함
    @Modifying
    @Query(value = "UPDATE Match m SET m.teamA = :winnerTeamName WHERE m.id = :nextMatchId AND m.teamA IS NULL", nativeQuery = true)
    int updateNextRoundTeamA(@Param("nextMatchId") Long nextMatchId, @Param("winnerTeamName") String winnerTeamName);

    @Modifying
    @Query(value = "UPDATE Match m SET m.teamB = :winnerTeamName WHERE m.id = :nextMatchId AND m.teamB IS NULL", nativeQuery = true)
    int updateNextRoundTeamB(@Param("nextMatchId") Long nextMatchId, @Param("winnerTeamName") String winnerTeamName);
}

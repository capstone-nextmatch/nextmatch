//박세준

package com.project.nextmatch.repository;

import com.project.nextmatch.domain.MatchResult; // MatchResult 엔티티 참조 (필수)
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // @Query 사용을 위해 추가
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 전적/경기 결과(MatchResult) 엔티티에 대한 데이터 접근 기능을 정의합니다.
 */
@Repository
public interface MatchResultRepository extends JpaRepository<MatchResult, Long> {

    // 💡 MatchService에서 사용하려는 쿼리 메서드 정의

    /**
     * 특정 회원 ID가 승리한 경기 수를 조회합니다. (MatchResult.isWin = true 인 항목 카운트)
     * @param memberId 회원 ID
     * @return 승리 횟수
     */
    @Query("SELECT COUNT(mr) FROM MatchResult mr WHERE mr.member.id = :memberId AND mr.isWin = true")
    int countWinsByMemberId(@Param("memberId") Long memberId);

    /**
     * 특정 회원 ID가 패배한 경기 수를 조회합니다. (MatchResult.isWin = false 인 항목 카운트)
     * @param memberId 회원 ID
     * @return 패배 횟수
     */
    @Query("SELECT COUNT(mr) FROM MatchResult mr WHERE mr.member.id = :memberId AND mr.isWin = false")
    int countLossesByMemberId(@Param("memberId") Long memberId);
}
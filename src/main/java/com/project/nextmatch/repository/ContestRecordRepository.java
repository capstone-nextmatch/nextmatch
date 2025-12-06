//박세준

package com.project.nextmatch.repository;

import com.project.nextmatch.domain.ContestRecord; // 🚨 실제 엔티티가 존재해야 합니다! 🚨
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 대회 기록(ContestRecord) 엔티티에 대한 데이터 접근 기능을 정의합니다.
 */
@Repository
public interface ContestRecordRepository extends JpaRepository<ContestRecord, Long> {

    /**
     * 특정 사용자 ID의 특정 순위(1, 2, 3) 횟수를 집계합니다.
     */
    int countByMemberIdAndPlace(Long memberId, int place);

    /**
     * 4등 이하의 순위를 기록한 횟수(예선 탈락으로 간주)를 집계합니다.
     * Spring Data JPA의 메서드 이름 쿼리 규칙을 사용합니다.
     */
    int countByMemberIdAndPlaceGreaterThan(Long memberId, int place);
}
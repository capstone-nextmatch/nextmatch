/**
 * Filename: WishlistRepository.java
 * Author: Sejun Park
 * Description: WishList 엔티티에 대한 데이터 접근 기능을 정의합니다.
 */
package com.project.nextmatch.repository;

import com.project.nextmatch.domain.Contest; // findByMemberAndContest를 위해 추가
import com.project.nextmatch.domain.Member; // findByMemberAndContest를 위해 추가
import com.project.nextmatch.domain.WishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional; // deleteBy... 메서드를 위해 추가
import java.util.Optional;
import java.util.List;

public interface WishListRepository extends JpaRepository<WishList, Long> {

    // 1. 엔티티 객체를 이용한 조회 (첫 번째 파일)
    Optional<WishList> findByMemberAndContest(Member member, Contest contest);

    // 2. 특정 회원 ID와 대회 ID를 이용한 조회 (두 파일 모두 존재)
    Optional<WishList> findByMember_IdAndContest_Id(Long memberId, Long contestId);

    // 3. 특정 회원의 WishList 개수를 세는 메서드 (첫 번째 파일)
    /**
     * 특정 회원 ID를 가진 WishList 엔티티의 총 개수를 반환합니다. (좋아요 개수 카운트)
     * @param memberId 회원 ID
     * @return 해당 회원이 등록한 좋아요 개수
     */
    long countByMember_Id(Long memberId);

    // 4. ID 기반 삭제 (첫 번째 파일)
    @Transactional
    void deleteByMember_IdAndContest_Id(Long memberId, Long contestId);

    // 5. 존재 여부만 빠르게 확인 (두 파일 모두 존재)
    boolean existsByMember_IdAndContest_Id(Long memberId, Long contestId);

    // 6. 특정 회원의 모든 관심 대회 목록을 등록 시간의 역순으로 조회 (두 파일 모두 존재)
    /**
     * 특정 회원의 모든 관심 대회 목록을 등록 시간의 역순으로 조회합니다.
     */
    List<WishList> findByMember_IdOrderByRegisteredAtDesc(Long memberId);
}
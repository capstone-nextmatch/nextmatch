package com.project.nextmatch.repository;

import com.project.nextmatch.domain.WishList; // 엔티티 경로 확인
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WishListRepository extends JpaRepository<WishList, Long> {

    /**
     * 특정 회원의 모든 관심 대회 목록을 등록 시간의 역순으로 조회합니다.
     */
    List<WishList> findByMember_IdOrderByRegisteredAtDesc(Long memberId);

    /**
     * 특정 회원 ID와 대회 ID를 통해 이미 등록되었는지 확인합니다.
     */
    Optional<WishList> findByMember_IdAndContest_Id(Long memberId, Long contestId);

    /**
     * 특정 회원 ID와 대회 ID를 통해 등록된 WishList 항목의 존재 여부만 확인합니다.
     */
    boolean existsByMember_IdAndContest_Id(Long memberId, Long contestId);
}
/**
 * Filename: ContestRepository.java
 * Author: Sejun Park
 */
package com.project.nextmatch.repository;

import com.project.nextmatch.domain.Contest;
import com.project.nextmatch.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Contest 엔티티 (PK 타입은 Long)를 다루는 Repository
public interface ContestRepository extends JpaRepository<Contest, Long> {

    //권동혁
    Optional<Contest> findByMember(Member member);
    // 필요한 경우, 나중에 커스텀 메서드를 추가할 수 있습니다.

}

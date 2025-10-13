//권동혁

package com.project.nextmatch.repository;

import com.project.nextmatch.domain.Event;
import com.project.nextmatch.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // 1. 검색어 없이 모든 목록을 가져올 때 (Fetch Join 사용)
    @Query("SELECT e FROM Event e JOIN FETCH e.member")
    List<Event> findAllWithMember();

    // 2. 검색어가 있을 때 (Fetch Join 사용)
    // 제목(title)에 검색어(search)가 포함된 이벤트 목록을 찾습니다. (대소문자 구분 없이)
    @Query("SELECT e FROM Event e JOIN FETCH e.member WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Event> findByTitleContainingWithMember(@Param("search") String search);

    // 모든 이벤트를 찾습니다.
    List<Event> findAll();
    Optional<Event> findByMember(Member member);
}

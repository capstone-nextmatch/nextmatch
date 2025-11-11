package com.project.nextmatch.repository;

import com.project.nextmatch.domain.Contest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import org.springframework.data.repository.query.Param;

public interface ContestListRepository extends JpaRepository<Contest, Long>{
    @Query("SELECT e FROM Contest e JOIN FETCH e.member")
    List<Contest> findAllWithMember();

    // 2. 검색어가 있을 때 (Fetch Join 사용)
    // 제목(title)에 검색어(search)가 포함된 이벤트 목록을 찾습니다. (대소문자 구분 없이)
    @Query("SELECT e FROM Contest e JOIN FETCH e.member WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Contest> findByTitleContainingWithMember(@Param("search") String search);

    // 모든 이벤트를 찾습니다.
    //List<Contest> findAll();
}

package com.project.nextmatch.repository;

import com.project.nextmatch.domain.Event;
import com.project.nextmatch.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Optional<Event> findByMember(Member member);
}

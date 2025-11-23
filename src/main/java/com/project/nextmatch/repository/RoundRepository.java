package com.project.nextmatch.repository;

import com.project.nextmatch.domain.Round;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoundRepository extends JpaRepository<Round, Long> {
    List<Round> findByContestId(Long id);
}

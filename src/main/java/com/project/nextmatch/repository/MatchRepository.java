package com.project.nextmatch.repository;

import com.project.nextmatch.domain.Match;
import com.project.nextmatch.domain.Round;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByRound(Round round);
}

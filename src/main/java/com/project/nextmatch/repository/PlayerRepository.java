package com.project.nextmatch.repository;

import com.project.nextmatch.domain.Player;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    List<Player> findAllByMemberIdInAndContestId( List<Long> memberId, Long contestId);
}

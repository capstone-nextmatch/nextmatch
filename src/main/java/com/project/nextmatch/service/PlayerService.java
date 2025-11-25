package com.project.nextmatch.service;

import com.project.nextmatch.domain.Contest;
import com.project.nextmatch.domain.Member;
import com.project.nextmatch.domain.Player;
import com.project.nextmatch.dto.MatchCreateRequest;
import com.project.nextmatch.repository.ContestRepository;
import com.project.nextmatch.repository.MemberRepository;
import com.project.nextmatch.repository.PlayerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final MemberRepository memberRepository;
    private final ContestRepository contestRepository;
    private final PlayerRepository playerRepository;

    @Transactional
    public void registerPlayers(MatchCreateRequest request) {

        // Contest 조회
        Contest contest = contestRepository.findById(request.getContestId())
                .orElseThrow(() -> new EntityNotFoundException("해당 대회가 존재하지 않습니다."));


        // Member ID 리스트로 Member 조회 후 Player 생성
        for (Long memberId : request.getMemberId()) {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다. ID=" + memberId));

            // 이미 존재하는 Player 조회
            Optional<Player> existing = playerRepository.findByContestIdAndMemberId(contest.getId(), memberId);

            if (existing.isEmpty()) {
                Player player = Player.builder()
                        .member(member)
                        .contest(contest)
                        .build();
                playerRepository.save(player);
            }
            // 이미 있으면 아무 것도 하지 않음 (재사용)
        }

    }

}

package com.project.nextmatch.service;

import com.project.nextmatch.domain.Member;
import com.project.nextmatch.dto.MemberUpdateRequestDto;
import com.project.nextmatch.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 테스트 케이스 4의 핵심: 내 정보 수정 폼에 필요한 현재 사용자 정보를 조회합니다.
     * @param memberId 로그인한 사용자의 ID
     * @return 정보 수정 폼 DTO
     */
    public MemberUpdateRequestDto getMemberInfoForUpdate(Long memberId) {
        // 1. DB에서 사용자 엔티티를 찾습니다.
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 엔티티를 DTO로 변환하여 Controller에 전달합니다.
        return MemberUpdateRequestDto.from(member);
    }
}
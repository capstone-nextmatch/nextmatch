//권동혁

package com.project.nextmatch.service;

import com.project.nextmatch.domain.Contest;
import com.project.nextmatch.domain.Member;
import com.project.nextmatch.dto.ContestCreateRequest;
import com.project.nextmatch.repository.ContestRepository;
import com.project.nextmatch.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Getter
@RequiredArgsConstructor
public class ContestService {

    private final MemberRepository memberRepository;
    private final ContestRepository contestRepository;

    public void contestCreate(ContestCreateRequest request) {
        Member member = memberRepository.findByUsername(request.getUsername()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        if (request.getStartDate().isAfter(request.getDeadlineDate())) {
            throw new IllegalArgumentException("시작일은 마감일보다 이전이어야 합니다");
        }


        //대회 생성
        Contest contest = Contest.builder()
                .member(member)
                .eventCategory(request.getContestCategory())
                .imageUrl(request.getImageUrl())
                .title(request.getTitle())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .deadlineDate(request.getDeadlineDate())
                .build();

        contestRepository.save(contest);



    };
}

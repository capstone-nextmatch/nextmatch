//권동혁

package com.project.nextmatch.service;

import com.project.nextmatch.domain.Contest;
import com.project.nextmatch.domain.Member;
import com.project.nextmatch.dto.ContestCreateRequest;
import com.project.nextmatch.repository.ContestRepository;
import com.project.nextmatch.repository.MemberRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

@Service
@Getter
@RequiredArgsConstructor
public class ContestService {

    private final MemberRepository memberRepository;
    private final ContestRepository contestRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Contest contestCreate(ContestCreateRequest request) {
        Member member = memberRepository.findByUsername(request.getUsername()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        if (request.getStartDate().isAfter(request.getDeadlineDate())) {
            throw new IllegalArgumentException("시작일은 마감일보다 이전이어야 합니다");
        }

        //입력값 정화
        String safeTitle = Jsoup.clean(request.getTitle(), Safelist.basic());
        String safeDescription = Jsoup.clean(request.getDescription(), Safelist.basic());
        String safeCategory = Jsoup.clean(request.getContestCategory(), Safelist.basic());
        String safeImageUrl = Jsoup.clean(request.getImageUrl(), Safelist.basic());


        //대회 생성
        Contest contest = Contest.builder()
                .member(member)
                .eventCategory(safeCategory)
                .imageUrl(safeImageUrl)
                .title(safeTitle)
                .description(safeDescription)
                .startDate(request.getStartDate())
                .deadlineDate(request.getDeadlineDate())
                .build();

        return contestRepository.save(contest);



    };
}

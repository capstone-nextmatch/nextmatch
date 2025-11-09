//이병철
package com.project.nextmatch.dto;

import com.project.nextmatch.domain.Contest;
import com.project.nextmatch.domain.Member;
import lombok.Getter;
import java.time.LocalDate;
import java.util.Optional; // Optional 임포트

@Getter
public class ContestListResponse {
    private final Long id;
    private final String title;
    private final String contestCategory;
    private final LocalDate contestDate;
    private final String memberUsername;

    public ContestListResponse(Contest contest) {
        this.id = contest.getId();
        this.title = contest.getTitle();
        this.contestCategory = contest.getEventCategory();
        this.contestDate = contest.getStartDate();

        // NullPointerException 방지 로직 적용
        this.memberUsername = Optional.ofNullable(contest.getMember())
                .map(Member::getUsername)
                .orElse("탈퇴한 사용자"); // null일 경우 기본값 지정
    }
}
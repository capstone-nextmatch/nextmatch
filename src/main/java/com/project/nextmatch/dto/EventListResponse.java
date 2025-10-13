//이병철
package com.project.nextmatch.dto;

import com.project.nextmatch.domain.Event;
import com.project.nextmatch.domain.Member;
import lombok.Getter;
import java.time.LocalDate;
import java.util.Optional; // Optional 임포트

@Getter
public class EventListResponse {
    private final Long id;
    private final String title;
    private final String eventCategory;
    private final LocalDate eventDate;
    private final String memberUsername;

    public EventListResponse(Event event) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.eventCategory = event.getEventCategory();
        this.eventDate = event.getEventDate();

        // NullPointerException 방지 로직 적용
        this.memberUsername = Optional.ofNullable(event.getMember())
                .map(Member::getUsername)
                .orElse("탈퇴한 사용자"); // null일 경우 기본값 지정
    }
}
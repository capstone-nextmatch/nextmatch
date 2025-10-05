package com.project.nextmatch.dto;

import com.project.nextmatch.domain.Event;
import lombok.Getter;
import java.time.LocalDate;

// EventService의 내부 DTO를 분리한 버전 (EventService 내부의 DTO는 제거해야 함)
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
        this.memberUsername = event.getMember().getUsername();
    }
}
package com.project.nextmatch.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

// EventService의 createEvent 메서드 컴파일 오류 해결을 위해 DTO를 정의합니다.
@Getter
@Setter
public class EventCreateRequest {
    // Member 엔티티를 찾기 위해 필요한 ID (예시)
    private Long memberId;

    // EventService에서 getTitle() 및 getEventCategory()로 접근합니다.
    private String title;
    private String eventCategory;
    private String description;
    private LocalDate eventDate;
    private LocalDate deadlineDate;
    private String imageUrl;
}
package com.project.nextmatch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventCreateRequest {

    private String username;
    private String eventCategory;
    private String imageUrl;
    private String description;
    private LocalDate eventDate;
    private LocalDate deadlineDate;
}

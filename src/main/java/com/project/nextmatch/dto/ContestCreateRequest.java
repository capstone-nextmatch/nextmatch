//권동혁

package com.project.nextmatch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContestCreateRequest {

    private String username;
    private String eventCategory;
    private String imageUrl;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate deadlineDate;
}

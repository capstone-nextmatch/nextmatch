//권동혁

package com.project.nextmatch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

    @NotBlank(message = "사용자 이름은 필수입니다.")
    private String username;

    @NotBlank(message = "대회 카테고리는 필수입니다.")
    private String contestCategory;

    private String imageUrl;

    @NotBlank(message = "대회 제목은 필수입니다.")
    @Pattern(
            regexp = "^[^<>\"';]+$",
            message = "대회명에 <, >, \", ', ; 문자는 사용할 수 없습니다"
    )
    private String title;

    private String description;

    @NotNull(message = "시작일은 필수입니다.")
    private LocalDate startDate;

    private LocalDate deadlineDate;
}

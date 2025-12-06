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

    // 두 번째 파일의 유효성 검사 어노테이션을 적용
    @NotBlank(message = "사용자 이름은 필수입니다.")
    private String username;

    // 두 번째 파일의 contestCategory 필드와 유효성 검사 어노테이션을 적용
    @NotBlank(message = "대회 카테고리는 필수입니다.")
    private String contestCategory;

    private String imageUrl;

    // 두 번째 파일의 유효성 검사 어노테이션을 적용
    @NotBlank(message = "대회 제목은 필수입니다.")
    @Pattern(
            regexp = "^[^<>\"';]+$",
            message = "대회명에 <, >, \", ', ; 문자는 사용할 수 없습니다"
    )
    private String title;

    private String description;

    // 두 번째 파일의 유효성 검사 어노테이션을 적용
    @NotNull(message = "시작일은 필수입니다.")
    private LocalDate startDate;

    private LocalDate deadlineDate;

    // 참고: 첫 번째 파일의 eventCategory는 두 번째 파일의 contestCategory로 대체되었습니다.
}
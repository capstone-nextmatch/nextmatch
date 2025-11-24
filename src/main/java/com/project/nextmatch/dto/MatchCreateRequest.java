package com.project.nextmatch.dto;

import com.project.nextmatch.domain.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MatchCreateRequest {

    @NotNull(message = "회원 ID 목록은 필수입니다.")
    @Size(min = 2, message = "최소 두 명 이상의 참가자가 필요합니다.")
    private List<Long> memberId;

    @NotNull(message = "대회 ID는 필수입니다.")
    private Long contestId;

}

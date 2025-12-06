//박세준
package com.project.nextmatch.dto;

import com.project.nextmatch.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder // 테스트에서 객체를 쉽게 만들기 위해 사용
public class MemberUpdateRequestDto {

    private Long id;
    private String username;
    private String email;
    // 전화번호 등의 추가 필드가 있다면 여기에 포함합니다.

    // Entity를 DTO로 변환하는 정적 메서드
    public static MemberUpdateRequestDto from(Member member) {
        return MemberUpdateRequestDto.builder()
                .id(member.getId())
                .username(member.getUsername())
                // .email(member.getEmail()) // Member 엔티티에 email이 있다면 사용
                .build();
    }
}

package com.project.nextmatch.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "members")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter // <--- 이 부분이 getName() 대신 getUsername()을 사용하기 위해 필요합니다.
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username; // EventService에서 getUsername()으로 접근합니다.

    @Column(nullable = false, length = 100)
    private String password;

    // 프로젝트에 MemberRepository가 필요하다면 여기에 추가적으로 정의해야 합니다.
}
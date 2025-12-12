//백송렬

package com.project.nextmatch.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "members")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false) // null 불가, 중복 불가 제약조건
    private String name;//이름

    @Column(nullable = false, length = 100)
    private String password;

    @Column(unique = true)
    private String phone;//회원 전화번호

    @Column(nullable = false, unique = true)
    private String email; //유저 이메일

    public Member(String username, String name, String password,
                  String phone, String email) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.email = email;

    }

}

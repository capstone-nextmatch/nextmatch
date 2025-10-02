package com.example.gaipLogin;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity // 이 클래스는 데이터베이스 테이블과 1:1로 매칭되는 객체라고 알려줍니다.
@Table(name = "users") // 1. 테이블 이름을 'users'로 지정 (예약어 충돌 방지)
@Getter
@NoArgsConstructor // 기본 생성자를 만들어줍니다.
public class User {

    @Id // 이 필드가 테이블의 기본 키(Primary Key)임을 나타냅니다.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID 값을 자동으로 생성해줍니다.
    private Long id; //회원번호

    @Column(nullable = false, unique = true)
    private String userId;//유저 아이디

    @Column(nullable = false, unique = true) // null 불가, 중복 불가 제약조건
    private String name;//이름

    @Column(nullable = false)
    private String password;//비밀번호

    @Column(unique = true)
    private String phone;//회원 전화번호

    @Column(nullable = false, unique = true)
    private String email; //유저 이메일

    

    public User(String userId, String name, String password,
                String phone, String email) {
        this.userId = userId;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.email = email;

    }
}
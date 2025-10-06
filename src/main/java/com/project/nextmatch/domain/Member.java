//권동혁

package com.project.nextmatch.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "members")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter //1006 백송렬 작성 WishService에서 오류가 나길래 추가 시켰습니다.
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

}

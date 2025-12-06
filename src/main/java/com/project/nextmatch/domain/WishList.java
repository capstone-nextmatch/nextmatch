/**
 * Filename: WishList.java
 * Author: Sejun Park
 * Description: 회원(Member)이 대회(Contest)에 대해 설정한 관심 목록(좋아요)을 나타내는 JPA 엔티티.
 * member_id와 contest_id 조합에 대한 유니크 제약 조건을 설정하여 중복 추가를 방지합니다.
 */
package com.project.nextmatch.domain;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(
        name = "wishlist",
        uniqueConstraints = {
                // 하나의 회원이 같은 대회를 두 번 '관심 목록'에 추가하지 못하도록 제약 조건 설정
                @UniqueConstraint(columnNames = {"member_id", "contest_id"})
        }
)
public class WishList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wish_id")
    private Long id; // 위시 번호 (PK)

    // 회원 (Member)과 다대일 관계 설정
    // Foreign Key: member_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 대회 (Contest)와 다대일 관계 설정
    // Foreign Key: contest_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id", nullable = false)
    private Contest contest;

    @Column(nullable = false)
    private LocalDateTime registeredAt; // 등록 시간

    // 첫 번째 파일에 있던 생성자를 유지합니다. (엔티티 생성 시 사용)
    public WishList(Member member, Contest contest) {
        this.member = member;
        this.contest = contest;
    }

    // 이 메서드는 관심 대회 추가 시 등록 시간을 자동 설정하기 위해 유용합니다.
    @PrePersist
    public void prePersist() {
        this.registeredAt = LocalDateTime.now();
    }
}
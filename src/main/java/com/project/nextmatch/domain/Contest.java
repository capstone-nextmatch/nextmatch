/**
 * Filename: Contest.java
 * Author: Sejun Park
 * Description: 대회 정보를 담는 JPA 엔티티 클래스.
 * 좋아요 기능 관련 필드와 메서드를 포함합니다.
 */
package com.project.nextmatch.domain;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Contest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contest_id")
    private Long id; // 대회 기본 키 (PK)

    @Column(nullable = false, length = 255)
    private String title; // 대회명

    private LocalDate startDate; // 대회 시작일

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private String eventCategory;

    private String status;

    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    // ✨ 첫 번째 파일에서 추가된 필드: 좋아요 수
    private int likeCount;

    private LocalDate deadlineDate;

    // 대진표 관련 다른 정보들 (예: 장소, 종목 등)은 여기에 추가될 수 있습니다.
    // (두 번째 파일의 주석만 유지하고 필드는 기존 필드들로 커버됩니다.)


    // ✨ 테스트 및 간편 생성을 위해 남겨둡니다. (String title, int likeCount)
    // 첫 번째 파일에 있던 테스트용 생성자를 유지합니다.
    public Contest(String title, int likeCount) {
        this.title = title;
        this.likeCount = likeCount;
    }

    // ✨ 좋아요 수 증가/감소 메서드 추가 (첫 번째 파일에서 추가됨)
    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) { // 좋아요 수가 음수가 되지 않도록 방지
            this.likeCount--;
        }
    }
}
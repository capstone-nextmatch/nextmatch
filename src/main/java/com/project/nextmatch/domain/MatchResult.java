//박세준
package com.project.nextmatch.domain;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 경기 결과 기록을 위한 엔티티 (전적 조회용)
 * 실제 경기(Match) 엔티티와는 별개로, 결과 및 승패 기록을 저장할 수 있습니다.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "match_result")
public class MatchResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match_result_id")
    private Long id;

    // 해당 기록이 어떤 회원(Member)에 대한 기록인지 (조회 기준)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 이 기록이 어떤 경기(Match)와 관련된 것인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    // 승리 여부 (true: 승리, false: 패배)
    @Column(nullable = false)
    private boolean isWin;

    // 기록 생성 시점
    private LocalDateTime recordedAt;

    @PrePersist
    public void prePersist() {
        this.recordedAt = LocalDateTime.now();
    }
}
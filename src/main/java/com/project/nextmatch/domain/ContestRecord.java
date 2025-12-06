//박세준
package com.project.nextmatch.domain;

import lombok.*;
import jakarta.persistence.*;

/**
 * 대회 기록 엔티티. (AwardService의 순위 집계에 사용됨)
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "contest_record")
public class ContestRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contest_record_id")
    private Long id;

    // 이 기록이 어떤 회원(Member)에 대한 기록인지 (조회 기준)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 이 기록이 어떤 대회(Contest)와 관련된 것인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id", nullable = false)
    private Contest contest;

    // 달성한 최종 순위 (1, 2, 3 등 또는 4 이상)
    @Column(nullable = false)
    private int place;

}

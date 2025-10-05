//이병철
package com.project.nextmatch.domain;

import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDate;

@Entity
@Table(name = "events")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private String eventCategory;

    private String status;

    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDate eventDate;

    private LocalDate deadlineDate;

    private String title;
}

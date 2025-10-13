//권동혁

package com.project.nextmatch.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "events")
@Builder
@AllArgsConstructor
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

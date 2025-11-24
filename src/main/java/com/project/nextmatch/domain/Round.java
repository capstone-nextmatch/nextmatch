package com.project.nextmatch.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Round {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 몇 번째 라운드인지 (예: 1 = 32강, 2 = 16강 ...)
    private int roundNumber;

    // 해당 라운드의 이름 (예: "32강", "16강", "8강" 등)
    private String name;

    // 이 라운드가 속한 토너먼트
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id")
    private Contest contest;

    // 이 라운드에 포함된 경기들
    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL)
    private List<Match> matches;

}

package work.wish.wishlist1.domain;

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

    @Column(length = 100)
    private String host; // 주최측

    // 대진표 관련 다른 정보들 (예: 장소, 종목 등)은 여기에 추가될 수 있습니다.
}
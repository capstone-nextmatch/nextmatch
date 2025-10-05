package work.wish.wishlist1.repository;

import work.wish.wishlist1.domain.Contest;
import org.springframework.data.jpa.repository.JpaRepository;

// Contest 엔티티 (PK 타입은 Long)를 다루는 Repository
public interface ContestRepository extends JpaRepository<Contest, Long> {
    // 필요한 경우, 나중에 커스텀 메서드를 추가할 수 있습니다.
}
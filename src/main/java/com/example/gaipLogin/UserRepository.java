package com.example.gaipLogin;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>{
    //username으로 사용자를 찾기 위한 메소드
    Optional<User> findByName(String name);
    Optional<User> findByPhone(String phone);
    Optional<User> findByEmail(String Email);
    Optional<User> findByUserId(String userId);

}

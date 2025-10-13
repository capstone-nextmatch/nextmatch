//1006 백송렬 작성
package com.project.nextmatch;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // 이 클래스를 설정 클래스로 선언합니다.
@EnableWebSecurity
public class SecurityConfig {

    @Bean // 이 메서드가 반환하는 객체를 Spring 컨테이너에 Bean으로 등록합니다.
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화 (API 테스트 시 편리)
                .authorizeHttpRequests(auth -> auth
                        // '/api/signup'과 '/api/login' 주소는 인증 없이 누구나 접근 가능
                        .requestMatchers("/**").permitAll()
                        // 나머지 모든 요청은 인증된 사용자만 접근 가능
                        .anyRequest().authenticated()

                )
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }
}

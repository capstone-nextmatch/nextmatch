//1006 백송렬 작성
package com.project.nextmatch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //1120 백송렬 작성
                .csrf(csrf -> csrf.disable()) //CSRF 보호 비활성화 (API 테스트 시 편리)
                .authorizeHttpRequests(auth -> auth
                        //누구나 들어갈 수 있는 곳 (로그인, 회원가입, 메인, 정적 리소스)
                        .requestMatchers("/", "/main", "/login", "/signup", "/api/signup", "/api/login").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/error").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()

                        //그 외 모든 페이지(마이페이지, 대회 생성 등)는 '로그인한 사람'만 접근 가능
                        .anyRequest().authenticated()

                )

                //3. 로그아웃 설정
                .logout(logout -> logout
                        .logoutUrl("/logout") //이 주소로 요청 오면 로그아웃
                        .logoutSuccessUrl("/main") //로그아웃 후 이동할 곳
                        .invalidateHttpSession(true) //세션 삭제
                        .deleteCookies("JSESSIONID") //쿠키 삭제
                )
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
}

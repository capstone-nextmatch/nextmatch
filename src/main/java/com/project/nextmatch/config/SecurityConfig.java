//백송렬 작성
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
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean // 비밀번호 암호화
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //@Bean // AuthenticationManager 주입용
    //public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
    //    return authenticationConfiguration.getAuthenticationManager();
    //}

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (API 테스트/폼 안 쓸 때 편의용)
                .csrf(csrf -> csrf.disable())

                // 인가 규칙
                .authorizeHttpRequests(auth -> auth
                        // 누구나 접근 가능
                        .requestMatchers("/", "/main", "/login", "/signup", "/api/signup", "/api/login", "/api/me").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/error").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        // 그 외는 로그인해야 접근 가능
                        .anyRequest().authenticated()
                )

                // 로그아웃 설정
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/main")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )

                // 헤더 설정 (H2 콘솔 + XSS + CSP 한 번에)
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())   // H2 콘솔용
                        .xssProtection(xss ->
                                xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)
                        )
                        .contentSecurityPolicy(csp ->
                                csp.policyDirectives("script-src 'self' 'unsafe-inline';")
                        )
                );

        return http.build();
    }
}



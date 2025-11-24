//백송렬 작성
package com.project.nextmatch.service;

import com.project.nextmatch.domain.Member;
import com.project.nextmatch.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //1. DB에서 username으로 회원 조회
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        //2. 스프링 시큐리티가 이해하는 'User' 객체로 변환하여 반환
        //(여기서 반환된 객체 안의 비밀번호와, 사용자가 입력한 비밀번호를 스프링이 자동으로 비교합니다)
        return User.builder()
                .username(member.getUsername())
                .password(member.getPassword()) //DB에 저장된 암호화된 비밀번호
                .roles("USER") // 본 권한 부여 (필요시 변경 가능)
                .build();
    }
}

//1006 백송렬 작성
package com.project.nextmatch.service;

import com.project.nextmatch.domain.Member;
import com.project.nextmatch.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service // 이 클래스를 서비스 계층의 컴포넌트로 선언합니다.
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입 메서드
    public void signup(String username, String name, String password,
                       String passwordConfirm, String phone, String email) {
        if (!password.equals(passwordConfirm)) {
            throw new IllegalArgumentException("비밀번호 확인이 일치하지 않습니다.");
        }
        // 이미 존재하는 사용자인지 확인
        memberRepository.findByName(name).ifPresent(user -> {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        });
        memberRepository.findByPhone(phone).ifPresent(User ->{
            throw new IllegalArgumentException("이미 존재하는 휴대전화 번호입니다.");
        });
        memberRepository.findByEmail(email).ifPresent(User -> {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        });

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        // 사용자 정보를 DB에 저장
        Member member = new Member(username, name, encodedPassword, phone, email);
        memberRepository.save(member);
    }
    //로그인 메서드
    public Member login(String username, String password){
        //1. 사용자 id로 db에서 사용자 정보 조회
        // findById의 결과가 없을 경우에 예외를 발생
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(()->new IllegalArgumentException("등록된 사용자가 없습니다"));

        //2. 입력된 비밀번호와 db의 암호화된 비밀번호가 일치하는지 비교
        //passwordEncoder.matches(입력된 평문 비번, 암호화된 비번)
        if(!passwordEncoder.matches(password, member.getPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        //성공시 사용자 정보 반환
        return member;
    }
}
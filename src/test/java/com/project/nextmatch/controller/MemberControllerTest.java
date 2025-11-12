package com.project.nextmatch.controller;

import com.project.nextmatch.repository.MemberRepository;
import com.project.nextmatch.service.MemberService;
import org.assertj.core.api.AssertionsForClassTypes;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional; //트랜잭션 추가

// (필요한 static import 목록)
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString; //에러 메시지 검증용
import static org.assertj.core.api.Assertions.assertThat; //DB 검증용
import com.project.nextmatch.domain.Member;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional //테스트 후 DB 롤백 @AfterEach + deleteAll() 대체
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired //@MockBean 대신 진짜 Repository 주입
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private void setupManyMembers(int count, String lastUsername, String lastPassword) {
        // 1~999명까지는 대충 만들기
        for (int i = 0; i < count - 1; i++) {
            Member member = new Member(
                    "testUser" + i,
                    "김테스트" + i,
                    passwordEncoder.encode("password1234!"), // (비번은 암호화해서 넣어야 함)
                    "0100000" + String.format("%04d", i),
                    "test" + i + "@test.com"
            );
            memberRepository.save(member);
        }

        // 1000번째 마지막 회원 (우리가 로그인할 회원)
        Member loginMember = new Member(
                lastUsername,
                "김로그인",
                passwordEncoder.encode(lastPassword), // (로그인할 비번 암호화)
                "01099999999",
                "login@test.com"
        );
        memberRepository.save(loginMember);
    }

    @Test
    @DisplayName("비밀번호_불일치")
    void signup_fail_password_not_ilchi() throws Exception {
        // 비밀번호와 비밀번호 확인이 서로 다른 경우
        String requestJson = """
                {
                  "username": "testuser",
                  "name": "김테스트",
                  "password": "password1234!",
                  "passwordConfirm": "password5678!", 
                  "phone": "010-1234-5678",
                  "email": "test@test.com"
                }
                """;

        // when: /api/signup으로 가짜 POST 요청을 보냈을 때
        mockMvc.perform(
                        post("/api/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                //400 에러를 기대
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("비밀번호 확인이 일치하지 않습니다."))) //Service의 에러 메시지 확인
                .andDo(print());
    }

    @Test
    @DisplayName("비밀번호_일치")
    void signup_success_ilchi() throws Exception {
        //(비밀번호 일치)
        String requestJson = """
                {
                  "username": "realuser",
                  "name": "김성공",
                  "password": "password1234!",
                  "passwordConfirm": "password1234!",
                  "phone": "010-5678-1234",
                  "email": "success@test.com"
                }
                """;

        // when: /api/signup으로 가짜 POST 요청 (진짜 Service와 DB까지 가야 함)
        mockMvc.perform(
                        post("/api/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                // then: 200 OK 응답을 기대
                .andExpect(status().isOk())
                .andExpect(content().string("회원가입이 성공적으로 완료되었습니다."))
                .andDo(print());

        //(통합 테스트의 핵심) 진짜 DB에 저장되었는지 확인
        Optional<Member> foundMember = memberRepository.findByUsername("realuser");
        assertThat(foundMember).isPresent(); // "realuser"가 DB에 존재하는지 확인
        assertThat(foundMember.get().getName()).isEqualTo("김성공"); // 이름이 맞는지 확인
    }

    @Test
    @DisplayName("회원가입_성공_전화번호_정규화 (하이픈 포함)")
    void signup_success_phone_with_hyphens() throws Exception {
        //하이픈이 있는 것
        String requestJson = """
                {
                  "username": "phoneUserHyphen",
                  "name": "김테스트",
                  "password": "password1234!",
                  "passwordConfirm": "password1234!",
                  "phone": "010-1234-5678",
                  "email": "phoneUserhyphen@test.com"
                }
                """;

        mockMvc.perform(
                        post("/api/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isOk());

        Optional<Member> foundMember = memberRepository.findByUsername("phoneUserHyphen");
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getPhone()).isEqualTo("01012345678"); // "씻겨진" 값과 일치하는지 확인
    }

    @Test
    @DisplayName("회원가입_성공_전화번호_정규화 공백 포함")
    void signup_success_phone_with_spaces() throws Exception {
        //이번에는 공백

        String requestJson = """
                {
                  "username": "phoneUserSpace",
                  "name": "김테스트",
                  "password": "password1234!",
                  "passwordConfirm": "password1234!",
                  "phone": "010 1234 5678",
                  "email": "phoneUserSpace@test.com"
                }
                """;

        // when:
        mockMvc.perform(
                        post("/api/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                // then (Part 1):
                .andExpect(status().isOk());

        // then (Part 2): DB 검증
        Optional<Member> foundMember = memberRepository.findByUsername("phoneUserSpace");
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getPhone()).isEqualTo("01012345678");
    }

    @Test
    @DisplayName("회원가입_성공_전화번호_정규화 (깨끗한 값)")
    void signup_success_phone_clean() throws Exception {
        // given: 1. "깨끗한" JSON
        String username = "phoneUserClean";
        String cleanPhone = "01012345678"; // 기대값

        String requestJson = """
                {
                  "username": "phoneUserClean",
                  "name": "김테스트",
                  "password": "password1234!",
                  "passwordConfirm": "password1234!",
                  "phone": "01012345678",
                  "email": "phoneUserClean@test.com"
                }
                """;

        mockMvc.perform(
                        post("/api/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )

                .andExpect(status().isOk());

        //DB 검증
        Optional<Member> foundMember = memberRepository.findByUsername("phoneUserClean");
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getPhone()).isEqualTo("01012345678"); // "씻겨진" 값과 일치하는지 확인
    }

    @Test
    @DisplayName("성능_테스트_회원_1000명_DB에서_로그인_1초_이내")
    void login_performance_with_1000_members() throws Exception {
        //DB에 1,000명의 가짜 회원을 저장
        //(로그인할 마지막 1명의 아이디와 비밀번호를 기억해 둡니다)
        String loginUser = "testUser999";
        String loginPass = "password1234!";
        setupManyMembers(1000, loginUser, loginPass);

        // 로그인에 사용할 JSON 준비
        String requestJson = String.format("""
                {
                  "username": "%s",
                  "password": "%s"
                }
                """, loginUser, loginPass);

        // 로그인 시간을 측정
        Instant startTime = Instant.now(); // <-- 1. 시간 측정 시작!

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk()) // 200 OK 응답을 기대
                .andDo(print());

        Instant endTime = Instant.now(); // <-- 2. 시간 측정 끝!

        // (검증):
        Duration duration = Duration.between(startTime, endTime); // 3. 총 걸린 시간 계산
        long responseTimeMillis = duration.toMillis();

        System.out.println("로그인 응답 시간 (1000명 DB): " + responseTimeMillis + "ms");

        //응답 시간이 1초(1000ms) 미만인지 검증!
        assertThat(responseTimeMillis).isLessThan(1000L);
    }

}
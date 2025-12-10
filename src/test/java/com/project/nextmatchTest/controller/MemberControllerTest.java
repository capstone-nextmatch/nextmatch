//백송렬 작성
package com.project.nextmatchTest.controller;

import com.project.nextmatch.domain.Member;
import com.project.nextmatch.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 대량 회원 생성 헬퍼
    private void setupManyMembers(int count, String lastUsername, String lastPassword) {
        for (int i = 0; i < count - 1; i++) {
            Member member = new Member(
                    "testUser" + i,
                    "김테스트" + i,
                    passwordEncoder.encode("password1234!"),
                    "0100000" + String.format("%04d", i),
                    "test" + i + "@test.com"
            );
            memberRepository.save(member);
        }

        Member loginMember = new Member(
                lastUsername,
                "김로그인",
                passwordEncoder.encode(lastPassword),
                "01099999999",
                "login@test.com"
        );
        memberRepository.save(loginMember);
    }

    @Test
    @DisplayName("비밀번호_불일치")
    void signup_fail_password_not_ilchi() throws Exception {
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

        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("비밀번호 확인이 일치하지 않습니다.")))
                .andDo(print());
    }

    @Test
    @DisplayName("비밀번호_일치")
    void signup_success_ilchi() throws Exception {
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

        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string("회원가입이 성공적으로 완료되었습니다."))
                .andDo(print());

        Optional<Member> foundMember = memberRepository.findByUsername("realuser");
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getName()).isEqualTo("김성공");
    }

    @Test
    @DisplayName("회원가입_성공_전화번호_정규화")
    void signup_success_phone_with_hyphens() throws Exception {
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

        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());

        Optional<Member> foundMember = memberRepository.findByUsername("phoneUserHyphen");
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getPhone()).isEqualTo("01012345678");
    }

    @Test
    @DisplayName("성능_테스트")
    void login_performance() throws Exception {
        String loginUser = "testUser999";
        String loginPass = "password1234!";
        setupManyMembers(100, loginUser, loginPass); // 1000명은 시간 걸리니 100명으로 줄여서 테스트

        String requestJson = String.format("""
                {
                  "username": "%s",
                  "password": "%s"
                }
                """, loginUser, loginPass);

        Instant startTime = Instant.now();

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andDo(print());

        Instant endTime = Instant.now();
        long responseTimeMillis = Duration.between(startTime, endTime).toMillis();
        System.out.println("로그인 소요 시간: " + responseTimeMillis + "ms");
        assertThat(responseTimeMillis).isLessThan(2000L);
    }

    @Test
    @DisplayName("로그인_후_me_성공")
    void me_success_after_login() throws Exception {
        // 1. 가입
        String signupJson = """
            {
              "username": "meuser",
              "name": "테스트유저",
              "password": "password1234!",
              "passwordConfirm": "password1234!",
              "phone": "01012345678",
              "email": "meuser@test.com"
            }
            """;
        mockMvc.perform(post("/api/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signupJson)).andExpect(status().isOk());

        // 2. 로그인
        String loginJson = """
            {
              "username": "meuser",
              "password": "password1234!"
            }
            """;
        MvcResult loginResult = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession();

        // 3. 확인
        mockMvc.perform(get("/api/me").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("meuser")));
    }

    @Test
    @DisplayName("비로그인_me_실패")
    void me_fail_without_login() throws Exception {
        mockMvc.perform(get("/api/me"))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }
}
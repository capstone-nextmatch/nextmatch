package com.project.nextmatch.controller;

import com.project.nextmatch.repository.MemberRepository;
import com.project.nextmatch.service.MemberService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

// (필요한 static import 목록)
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest2 {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown() {
        // 테스트 중에 생성된 모든 데이터를 지워서 다음 테스트에 영향을 주지 않도록 함
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입_실패_비밀번호에_특수문자_없음")
    void signup_fail_password_no_special_character() throws Exception {
        // password 필드에 특수문자가 없는 "password1234"를 보냄
        String requestJson = """
                {
                  "username": "specialuser",
                  "name": "김특수",
                  "password": "password1234",
                  "passwordConfirm": "password1234",
                  "phone": "010-11111-1111",
                  "email": "special1@test.com"
                }
                """;

        // /api/signup으로 가짜 POST 요청을 보냈을 때
        mockMvc.perform(
                        post("/api/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                // @Pattern 규칙에 걸려서 400 Bad Request 에러가 터져야 함
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입_성공_비밀번호에_특수문자_있음")
    void signup_success_password_with_special_character() throws Exception {
        // password 필드에 특수문자가 있는 "password1234!"를 보냄
        String requestJson = """
                {
                  "username": "specialUser",
                  "name": "김특수",
                  "password": "password1234!",
                  "passwordConfirm": "password1234!",
                  "phone": "010-1234-1234",
                  "email": "special@test.com"
                }
                """;

        // /api/signup으로 가짜 POST 요청을 보냈을 때
        mockMvc.perform(
                        post("/api/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                // @Pattern 규칙에 의해 200 Ok 가 나와야 함
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입_실패_비밀번호_7자리")
    void signup_fail_password_too_short() throws Exception {
        //7자리 비밀번호
        String requestJson = """
                {
                  "username": "testuser",
                  "name": "김테스트",
                  "password": "pass12!",
                  "passwordConfirm": "pass12!",
                  "phone": "010-1234-5678",
                  "email": "test@test.com"
                }
                """;

        mockMvc.perform(
                        post("/api/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson) // 이 내용을 담아서
                )
                //400 Bad Request 에러가 터져야 성공
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입_성공_비밀번호_8자리")
    void signup_success_password_8_isang() throws Exception {
        //8자리 비밀번호
        String requestJson = """
                {
                  "username": "testuser",
                  "name": "김테스트",
                  "password": "pass1234!",
                  "passwordConfirm": "pass1234!",
                  "phone": "010-1234-5678",
                  "email": "test@test.com"
                }
                """;

        mockMvc.perform(
                        post("/api/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )

                .andExpect(status().isOk()) // 200 OK를 기대함
                .andDo(print());
    }



    @Test
    @DisplayName("성능_테스트_100명_동시_회원가입_스트레스_테스트")
    void signup_stress_test_with_100_concurrent_users() throws Exception {

        //100명의 가상 유저(스레드)가 '동시에' 가입할 준비
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startGate = new CountDownLatch(1); // "출발!" 신호탄 (1번)
        CountDownLatch endGate = new CountDownLatch(threadCount); // "100명 도착!" 신호

        //스레드들의 '결과(상태 코드)'를 담을 리스트 (스레드 안전 버전)
        List<Integer> statusCodes = Collections.synchronizedList(new ArrayList<>());

        //100명의 가상 유저가 출발선에 대기
        for (int i = 0; i < threadCount; i++) {
            final int userIndex = i;
            executorService.submit(() -> {
                try {
                    //1."i"번째 유저를 위한 고유한 JSON 생성
                    String uniqueUsername = "loadTestUser" + userIndex;
                    String uniqueEmail = "load" + userIndex + "@test.com";
                    String uniquePhone = "010" + String.format("%08d", userIndex);

                    String requestJson = String.format("""
                            {
                              "username": "%s",
                              "name": "부하테스터%d",
                              "password": "password1234!",
                              "passwordConfirm": "password1234!",
                              "phone": "%s",
                              "email": "%s"
                            }
                            """, uniqueUsername, userIndex, uniquePhone, uniqueEmail);

                    startGate.await(); //"출발!" 신호가 올 때까지 대기

                    //출발! 신호를 받으면 동시에 MockMvc 요청을 보냄
                    MvcResult result = mockMvc.perform(
                                    post("/api/signup")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(requestJson)
                            )
                            .andReturn();

                    statusCodes.add(result.getResponse().getStatus());

                } catch (Exception e) {
                    statusCodes.add(500); //테스트 자체에서 에러나면 500으로 기록
                } finally {
                    endGate.countDown(); //"나 끝났음!"
                }
            });
        }

        //출발! (신호탄 발사)
        startGate.countDown();
        //100명의 유저가 모두 끝날 때까지 30초간 대기
        endGate.await(30, TimeUnit.SECONDS);
        executorService.shutdown();

        //100개의 응답이 statusCodes 리스트에 담겨있어야 함
        assertThat(statusCodes).hasSize(threadCount);

        //100개의 응답이 "단 하나도 빠짐없이" '200 OK'여야 한다
        assertThat(statusCodes).allMatch(status -> status == 200);

        //DB에도 "정확히" 100명이 저장되었는지 확인
        assertThat(memberRepository.count()).isEqualTo(threadCount);
    }
}
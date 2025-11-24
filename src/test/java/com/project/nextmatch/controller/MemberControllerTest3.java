package com.project.nextmatch.controller;

import com.project.nextmatch.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

// 동시성 테스트를 위한 도구들
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest3 {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown() {
        // "각" 테스트가 끝날 때마다 DB를 싹 다 지움
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입_실패_동시성_경쟁_상태_방어_검증")
    void signup_fail_concurrency_race_condition() throws Exception {
        //2명의 가상 유저(스레드)가 '동시에' 가입할 준비
        int threadCount = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch endGate = new CountDownLatch(threadCount);

        String requestJson = """
                {
                  "username": "concurrentUser",
                  "name": "김동시",
                  "password": "password1234!",
                  "passwordConfirm": "password1234!",
                  "phone": "010-1111-2222",
                  "email": "concurrent@test.com"
                }
                """;
        //2명 스레드 담을 그릇
        List<Integer> statusCodes = Collections.synchronizedList(new ArrayList<>());

        //2명의 유저가 출발선에 대기
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    startGate.await(); // "출발!" 신호 대기
                    MvcResult result = mockMvc.perform(
                                    post("/api/signup")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(requestJson)
                            )
                            .andReturn();
                    statusCodes.add(result.getResponse().getStatus());
                } catch (Exception e) {
                    statusCodes.add(-1);
                    e.printStackTrace();
                } finally {
                    endGate.countDown();
                }
            });
        }

        startGate.countDown();
        endGate.await(10, TimeUnit.SECONDS);
        executorService.shutdown();


        assertThat(statusCodes).hasSize(threadCount);
        //1놈은 200(성공), 1놈은 400(중복)이 떠야 함
        assertThat(statusCodes).contains(200);
        assertThat(statusCodes).contains(400);

        //DB에 "정확히" 1명만 저장되었는지 확인
        assertThat(memberRepository.count()).isEqualTo(1);
    }

}
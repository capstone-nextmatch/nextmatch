package com.project.nextmatchTest.service;

import com.project.nextmatch.domain.Contest;
import com.project.nextmatch.domain.Member;
import com.project.nextmatch.dto.ContestCreateRequest;
import com.project.nextmatch.repository.ContestRepository;
import com.project.nextmatch.repository.MemberRepository;
import com.project.nextmatch.service.ContestService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ContestServiceTest {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ContestService contestService;

    @Autowired
    private ContestRepository contestRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private Validator validator;

    @AfterEach
    void cleanUp() {
        contestRepository.deleteAll();
        memberRepository.deleteAll();
    }

    //권동혁
    @Test
    void create_event() {
        Member member2 = memberRepository.save(Member.builder().username("tester").password("test123").build());

        ContestCreateRequest request = ContestCreateRequest.builder()
                .username("tester")
                .contestCategory("ffdsdf")
                .title("대회")
                .imageUrl("///")
                .description("fdsfdsf")
                .startDate(LocalDate.of(2025, 9, 23))
                .deadlineDate(LocalDate.of(2025, 9, 25))
                .build();

        contestService.contestCreate(request);


    }


    @Test
    @DisplayName("1. 로그인되지 않은 사용자 요청 실패")
    void eventCreate_fail_whenUserNotFound() {
        // given
        ContestCreateRequest request = ContestCreateRequest.builder()
                .username("ghost")
                .contestCategory("축구")
                .title("2025농구대회")
                .imageUrl("///")
                .description("fdsfdsf")
                .startDate(LocalDate.now().plusDays(1))
                .deadlineDate(LocalDate.now().plusDays(2))
                .build();

        // when & then
        assertThatThrownBy(() -> contestService.contestCreate(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @Test
    @Transactional
    @DisplayName("2. 필수 입력값 누락 시 실패")
    void eventCreate_fail_whenRequiredFieldMissing() {
        // given
        Member member = memberRepository.save(Member.builder().username("tester").password("test123").build());

        ContestCreateRequest request = ContestCreateRequest.builder()
                .username("tester")
                .contestCategory(null) // 필수값 누락
                .title("2025농구대회")
                .imageUrl("///")
                .description("fdsfdsf")
                .startDate(LocalDate.now().plusDays(1))
                .deadlineDate(LocalDate.now().plusDays(2))
                .build();

        // when
        Set<ConstraintViolation<ContestCreateRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isNotEmpty(); // 검증 오류가 발생해야 함
        String msg = violations.iterator().next().getMessage();
        System.out.println(msg);
        AssertionsForClassTypes.assertThat(violations.iterator().next().getMessage())
                .contains("필수"); // DTO에 설정한 message 확인 가능

    }

    @Test
    @DisplayName("3. 한 사용자가 중복 등록 가능")
    void eventCreate_success_whenDuplicateRegistration() {
        // given
        Member member = memberRepository.save(Member.builder().username("tester").password("test123").build());

        ContestCreateRequest request = ContestCreateRequest.builder()
                .username("tester")
                .contestCategory("농구")
                .title("2025농구대회")
                .imageUrl("///")
                .description("fdsfdsf")
                .startDate(LocalDate.now().plusDays(1))
                .deadlineDate(LocalDate.now().plusDays(2))
                .build();

        // when
        contestService.contestCreate(request);
        contestService.contestCreate(request);

        // then
        List<Contest> contests = contestRepository.findAllWithMember();
        assertThat(contests).hasSize(2);
        AssertionsForClassTypes.assertThat(contests.get(0).getMember().getUsername()).isEqualTo("tester");
    }

    @Test
    @Transactional
    @DisplayName("4. 잘못된 입력값 처리 (공백) - Validator 수동 검증")
    void eventCreate_fail_whenInvalidInput_withValidator() {
        // given
        Member member = memberRepository.save(
                Member.builder().username("tester").password("test123").build()
        );

        ContestCreateRequest request = ContestCreateRequest.builder()
                .username("tester")
                .contestCategory("   ") // 공백만 입력
                .title("2025농구대회")
                .imageUrl("///")
                .description("fdsfdsf")
                .startDate(LocalDate.now().plusDays(1))
                .deadlineDate(LocalDate.now().plusDays(2))
                .build();

        // when
        Set<ConstraintViolation<ContestCreateRequest>> violations = validator.validate(request);

        // then

        assertThat(violations).isNotEmpty(); // 검증 오류가 발생해야 함
        String msg = violations.iterator().next().getMessage();
        System.out.println(msg);
        AssertionsForClassTypes.assertThat(violations.iterator().next().getMessage())
                .contains("필수"); // DTO에 설정한 message 확인 가능
    }

    @Test
    @DisplayName("5. XSS 입력값 필터링 검증")
    void eventCreate_fail_xssDetected() {

        Member member = memberRepository.save(
                Member.builder().username("tester").password("test123").build()
        );

        String xss = "<img src=x onerror=alert('xss')>";
        ContestCreateRequest request = ContestCreateRequest.builder()
                .username("tester")
                .contestCategory("축구")
                .title(xss)
                .imageUrl("///")
                .description("fdsfdsf")
                .startDate(LocalDate.now().plusDays(1))
                .deadlineDate(LocalDate.now().plusDays(2))
                .build();

        Contest contest = contestService.contestCreate(request);

        AssertionsForClassTypes.assertThat(contest.getTitle()).doesNotContain("<script>").doesNotContain("onerror");
    }

    @Test
    @Transactional
    @DisplayName("6. 대량 Contest 저장 성능 및 데이터 검증 (배치 처리)")
    void contestCreate_bulkInsert_performanceAndVerification() {
        Member member = memberRepository.save(
                Member.builder().username("tester").password("test123").build()
        );

        long bulkSize = 100_000L;   // 총 저장할 데이터 개수 (디스크 용량 고려 백만)
        int batchSize = 1_000;         // 한 번에 저장할 배치 크기

        long start = System.currentTimeMillis();

        List<Contest> contests = new ArrayList<>(batchSize);
        for (long i = 0; i < bulkSize; i++) {
            contests.add(Contest.builder()
                    .member(member)
                    .eventCategory("농구")
                    .title("농구대회-" + i)
                    .description("설명-" + i)
                    .imageUrl("http://example.com/image" + i + ".png")
                    .startDate(LocalDate.now().plusDays(1))
                    .deadlineDate(LocalDate.now().plusDays(2))
                    .build());

            if (contests.size() == batchSize) {
                System.out.println((i + 1) + "차--------------------------------------------------- 배치처리");
                em.flush();   // DB 반영
                em.clear();   // 영속성 컨텍스트 비우기
                contestRepository.saveAll(contests);
                contests.clear();
            }
        }

        if (!contests.isEmpty()) {
            contestRepository.saveAll(contests);
        }

        long end = System.currentTimeMillis();
        System.out.println("총 소요 시간(ms): " + (end - start));

        // ✅ 저장 검증
        long count = contestRepository.count();
        AssertionsForClassTypes.assertThat(count).isEqualTo(bulkSize);

        // ✅ 무결성 검증 (샘플 조회)
        Contest first = contestRepository.findByTitle("농구대회-0").orElseThrow();
        Contest last = contestRepository.findByTitle("농구대회-" + (bulkSize - 1)).orElseThrow();

        AssertionsForClassTypes.assertThat(first.getMember().getUsername()).isEqualTo("tester");
        AssertionsForClassTypes.assertThat(last.getTitle()).isEqualTo("농구대회-" + (bulkSize - 1));
    }

    @Test
    @Sql(statements = {"DELETE FROM contest", "DELETE FROM members"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @DisplayName("7. DB 커넥션 동시성 테스트")
    void connectionConcurrencyTest() throws InterruptedException {
        Member member = memberRepository.save(
                Member.builder().username("tester").password("test123").build()
        );

        int threadCount = 20;       // 동시에 실행할 쓰레드 수
        int insertsPerThread = 100; // 각 쓰레드가 insert할 건수

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int t = 0; t < threadCount; t++) {
            executor.submit(() -> {
                try {
                    for (int i = 0; i < insertsPerThread; i++) {
                        Contest contest = Contest.builder()
                                .member(member)
                                .eventCategory("농구")
                                .title("동시성-" + Thread.currentThread().getId() + "-" + i)
                                .description("테스트")
                                .imageUrl("http://example.com/img.png")
                                .startDate(LocalDate.now())
                                .deadlineDate(LocalDate.now().plusDays(1))
                                .build();
                        contestRepository.save(contest);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 쓰레드 종료 대기
        executor.shutdown();

        long count = contestRepository.count();
        System.out.println("총 저장된 Contest 개수: " + count);

        AssertionsForClassTypes.assertThat(count).isEqualTo(threadCount * insertsPerThread);
    }


    @Test
    @DisplayName("8. 시작일이 종료일보다 늦은 경우 저장 거부")
    void eventCreate_fail_whenStartDateAfterDeadline() {
        // given
        Member member = memberRepository.save(
                Member.builder().username("tester").password("test123").build()
        );

        ContestCreateRequest request = ContestCreateRequest.builder()
                .username("tester")
                .contestCategory("측구")
                .title("농구대회")
                .imageUrl("///")
                .description("fdsfdsf")
                .startDate(LocalDate.now().plusDays(5))   // 시작일
                .deadlineDate(LocalDate.now().plusDays(1)) // 종료일보다 늦음
                .build();

        // when & then
        assertThatThrownBy(() -> contestService.contestCreate(request))
                .isInstanceOf(IllegalArgumentException.class) // 혹은 커스텀 예외
                .hasMessageContaining("시작일은 마감일보다 이전이어야 합니다");
    }

    @Test
    @Transactional
    @DisplayName("9. 대회명에 특수문자 포함 시 검증 실패")
    void eventCreate_fail_whenTitleContainsSpecialChars() {
        // given
        ContestCreateRequest request = ContestCreateRequest.builder()
                .username("tester")
                .contestCategory("축구")
                .title("농구대회<>;") // 특수문자 포함
                .imageUrl("///")
                .description("fdsfdsf")
                .startDate(LocalDate.now().plusDays(1))
                .deadlineDate(LocalDate.now().plusDays(2))
                .build();

        // when
        Set<ConstraintViolation<ContestCreateRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isNotEmpty();
        AssertionsForClassTypes.assertThat(violations.iterator().next().getMessage())
                .contains("대회명에 <, >, \", ', ; 문자는 사용할 수 없습니다");
    }

    @Test
    @Transactional
    @DisplayName("10.1 트랜잭션 자동 롤백 검증")
    void rollbackTest() {
        assertThrows(RuntimeException.class, () -> {
            memberRepository.save(Member.builder().username("tester").password("test123").build());
            throw new RuntimeException("강제 예외");
        });
    }

    @Test
    @DisplayName("10.2 트랜잭션 커밋 동작과 REQUIRES_NEW 전파 옵션 검증")
    void requiresNewTest() {
        memberRepository.save(Member.builder().username("tester").password("test123").build()); // 즉시 커밋됨

        ContestCreateRequest request = ContestCreateRequest.builder()
                .username("tester")
                .contestCategory("축구")
                .title("농구대회<>;") // 특수문자 포함
                .imageUrl("///")
                .description("fdsfdsf")
                .startDate(LocalDate.now().plusDays(1))
                .deadlineDate(LocalDate.now().plusDays(2))
                .build();

        contestService.contestCreate(request); // 내부에서 REQUIRES_NEW → 별도 트랜잭션 커밋
        assertThat(contestRepository.findAll()).hasSize(1);
    }
}
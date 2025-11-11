package com.project.nextmatch.service;

import com.project.nextmatch.domain.Contest;
import com.project.nextmatch.domain.Member;
<<<<<<< HEAD
import com.project.nextmatch.dto.ContestCreateRequest;
import com.project.nextmatch.repository.ContestRepository;
import com.project.nextmatch.repository.MemberRepository;
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
                System.out.println((i+1) + "차--------------------------------------------------- 배치처리");
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
    @Sql(statements = {"DELETE FROM contest", "DELETE FROM members"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
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

=======
import com.project.nextmatch.dto.ContestListResponse;
import com.project.nextmatch.repository.ContestListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContestServiceTest {

    @Mock
    private ContestListRepository contestListRepository;

    @InjectMocks
    private ContestListService contestListService;

    private Contest mockContest;
    private Member mockMember;

    @BeforeEach
    void setUp() {
        mockMember = Member.builder()
                .id(10L)
                .username("managerUser")
                .password("testPw")
                .build();

        mockContest = Contest.builder()
                .id(1L)
                .title("테스트 토너먼트")
                .eventCategory("축구")
                .startDate(LocalDate.of(2026, 1, 1))
                .deadlineDate(LocalDate.of(2025, 12, 1))
                .member(mockMember) // manager_id 연결
                .status("RECRUITING")
                .build();
    }

    // --- 1. 목록 표시 및 데이터 유효성 테스트 ---

    @Test
    @DisplayName("T1. 정상 목록 표시: 검색어 없이 모든 대회가 DTO로 변환되어 반환된다")
    void shouldReturnAllContestsAsListResponseWhenNoSearchTerm() {
        // Given: Repository가 하나의 Contest 목록을 반환하도록 설정
        when(contestListRepository.findAllWithMember()).thenReturn(List.of(mockContest));

        // When: listEvents를 검색어 없이 호출
        List<ContestListResponse> result = contestListService.listEvents(null);

        // Then:
        // 1. Repository의 findAllWithMember() 메서드가 한 번 호출되었는지 확인
        verify(contestListRepository, times(1)).findAllWithMember();
        // 2. 결과가 비어 있지 않고 크기가 1인지 확인
        assertThat(result).hasSize(1);
        // 3. 반환된 DTO의 데이터가 유효한지 확인
        assertThat(result.get(0).getTitle()).isEqualTo("테스트 토너먼트");
        // DTO가 managerUsername을 member에서 가져오는지 확인
        assertThat(result.get(0).getMemberUsername()).isEqualTo("managerUser");
    }

    @Test
    @DisplayName("T2. 목록 없음: 등록된 대회가 없을 때 빈 목록을 반환한다")
    void shouldReturnEmptyListWhenNoContestsExist() {
        // Given: Repository가 빈 목록을 반환하도록 설정
        when(contestListRepository.findAllWithMember()).thenReturn(Collections.emptyList());

        // When: listEvents를 검색어 없이 호출
        List<ContestListResponse> result = contestListService.listEvents("");

        // Then:
        verify(contestListRepository, times(1)).findAllWithMember();
        assertThat(result).isEmpty();
    }

    // --- 2. 검색 및 필터링 기능 테스트 ---

    @Test
    @DisplayName("T3. 제목 검색 (부분 일치): 검색어를 포함하는 대회 목록을 반환한다")
    void shouldReturnFilteredContestsWhenSearchTermIsProvided() {
        // Given: 검색어 설정
        String searchTerm = "토너먼트";
        // Repository가 검색어로 필터링된 Contest 목록을 반환하도록 설정
        when(contestListRepository.findByTitleContainingWithMember(searchTerm)).thenReturn(List.of(mockContest));

        // When: listEvents를 검색어와 함께 호출
        List<ContestListResponse> result = contestListService.listEvents(searchTerm);

        // Then:
        // 1. findAllWithMember()는 호출되지 않고, findByTitleContainingWithMember()만 호출되었는지 확인
        verify(contestListRepository, never()).findAllWithMember();
        verify(contestListRepository, times(1)).findByTitleContainingWithMember(searchTerm);
        // 2. 결과가 비어 있지 않고 크기가 1인지 확인
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).contains(searchTerm);
    }

    // --- 3. 네거티브 및 보안 테스트 ---

    @Test
    @DisplayName("T4. 검색 결과 없음: 존재하지 않는 검색어로 조회 시 빈 목록을 반환한다")
    void shouldReturnEmptyListWhenSearchTermYieldsNoResults() {
        // Given: Repository가 특정 검색어에 대해 빈 목록을 반환하도록 설정
        String nonExistentSearchTerm = "없는대회";
        when(contestListRepository.findByTitleContainingWithMember(nonExistentSearchTerm)).thenReturn(Collections.emptyList());

        // When: listEvents를 존재하지 않는 검색어와 함께 호출
        List<ContestListResponse> result = contestListService.listEvents(nonExistentSearchTerm);

        // Then:
        verify(contestListRepository, times(1)).findByTitleContainingWithMember(nonExistentSearchTerm);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("T5. SQL Injection 방어: 악의적인 검색어도 문자열 그대로 처리한다")
    void shouldHandleMaliciousSearchTermsAsLiterals() {
        // Given: SQL Injection 공격 문자열
        String maliciousSqlSearch = "' OR 1=1 --";

        // Repository가 공격 문자열에 대해 빈 목록을 반환하도록 설정
        when(contestListRepository.findByTitleContainingWithMember(maliciousSqlSearch)).thenReturn(Collections.emptyList());

        // When: 악의적인 SQL 문자열로 호출
        contestListService.listEvents(maliciousSqlSearch);

        // Then: Repository 메서드가 문자열 그대로 받아서 호출되었는지 확인
        // (Service는 문자열을 그대로 전달하며, JPA가 파라미터 바인딩으로 공격을 방어합니다.)
        verify(contestListRepository, times(1)).findByTitleContainingWithMember(maliciousSqlSearch);
    }
>>>>>>> 5407a21dec356fc9059151ad168d1868dc7ce599
}
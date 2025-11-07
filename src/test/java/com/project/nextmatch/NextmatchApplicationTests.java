package com.project.nextmatch;

import com.project.nextmatch.domain.Contest;
import com.project.nextmatch.domain.Member;
import com.project.nextmatch.dto.ContestCreateRequest;
import com.project.nextmatch.repository.ContestRepository;
import com.project.nextmatch.repository.MemberRepository;
import com.project.nextmatch.service.ContestService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@Transactional
class NextmatchApplicationTests {

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


	@Test
	void contextLoads() {
	}

	//권동혁
	@Test
	void create_event() {
		Member member2 = memberRepository.save(Member.builder().username("isd").password("123456718").build());


		ContestCreateRequest request = ContestCreateRequest.builder()
				.username("isd")
				.contestCategory("ffdsdf")
				.imageUrl("///")
				.title("대회")
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
				.startDate(LocalDate.now().plusDays(1))
				.deadlineDate(LocalDate.now())
				.build();

		// when & then
		assertThatThrownBy(() -> contestService.contestCreate(request))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("존재하지 않는 회원입니다.");
	}

	@Test
	@DisplayName("2. 필수 입력값 누락 시 실패")
	void eventCreate_fail_whenRequiredFieldMissing() {
		// given
		Member member = memberRepository.save(Member.builder().username("tester").password("test123").build());

		ContestCreateRequest request = ContestCreateRequest.builder()
				.username("tester")
				.contestCategory(null) // 필수값 누락
				.title("2025농구대회")
				.startDate(LocalDate.now().plusDays(1))
				.deadlineDate(LocalDate.now())
				.build();

		// when
		Set<ConstraintViolation<ContestCreateRequest>> violations = validator.validate(request);

		// then
		assertThat(violations).isNotEmpty(); // 검증 오류가 발생해야 함
		String msg = violations.iterator().next().getMessage();
		System.out.println(msg);
		assertThat(violations.iterator().next().getMessage())
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
				.startDate(LocalDate.now().plusDays(1))
				.deadlineDate(LocalDate.now().plusDays(2))
				.build();

		// when
		contestService.contestCreate(request);
		contestService.contestCreate(request);

		// then
		List<Contest> events = contestRepository.findAll();
		assertThat(events).hasSize(2);
		AssertionsForClassTypes.assertThat(events.get(0).getMember().getUsername()).isEqualTo("tester");
	}

	@Test
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
				.startDate(LocalDate.now().plusDays(1))
				.deadlineDate(LocalDate.now())
				.build();

		// when
		Set<ConstraintViolation<ContestCreateRequest>> violations = validator.validate(request);

		// then

		assertThat(violations).isNotEmpty(); // 검증 오류가 발생해야 함
		String msg = violations.iterator().next().getMessage();
		System.out.println(msg);
		assertThat(violations.iterator().next().getMessage())
				.contains("필수"); // DTO에 설정한 message 확인 가능
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
				.startDate(LocalDate.now().plusDays(5))   // 시작일
				.deadlineDate(LocalDate.now().plusDays(1)) // 종료일보다 늦음
				.build();

		// when & then
		assertThatThrownBy(() -> contestService.contestCreate(request))
				.isInstanceOf(IllegalArgumentException.class) // 혹은 커스텀 예외
				.hasMessageContaining("시작일은 마감일보다 이전이어야 합니다");
	}

	@Test
	@DisplayName("9. 대회명에 특수문자 포함 시 검증 실패")
	void eventCreate_fail_whenTitleContainsSpecialChars() {
		// given
		ContestCreateRequest request = ContestCreateRequest.builder()
				.username("tester")
				.contestCategory("축구")
				.title("농구대회<>;") // 특수문자 포함
				.startDate(LocalDate.now().plusDays(1))
				.deadlineDate(LocalDate.now().plusDays(2))
				.build();

		// when
		Set<ConstraintViolation<ContestCreateRequest>> violations = validator.validate(request);

		// then
		assertThat(violations).isNotEmpty();
		assertThat(violations.iterator().next().getMessage())
				.contains("대회명에 <, >, \", ', ; 문자는 사용할 수 없습니다");
	}


}

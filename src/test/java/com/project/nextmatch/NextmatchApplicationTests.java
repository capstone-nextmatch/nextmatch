package com.project.nextmatch;

import com.project.nextmatch.domain.Member;
import com.project.nextmatch.dto.ContestCreateRequest;
import com.project.nextmatch.repository.ContestRepository;
import com.project.nextmatch.repository.MemberRepository;
import com.project.nextmatch.service.ContestService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
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

	@AfterEach
	void cleanUp() {
		contestRepository.deleteAll();
		memberRepository.deleteAll();
	}



	@Test
	void contextLoads() {
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


}

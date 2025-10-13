package com.project.nextmatch;

import com.project.nextmatch.domain.Member;
import com.project.nextmatch.dto.ContestCreateRequest;
import com.project.nextmatch.service.ContestService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;

@SpringBootTest
@Transactional
class NextmatchApplicationTests {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private ContestService contestService;

	@Test
	void contextLoads() {
	}

	//권동혁
	@Test
	@Rollback(value = false)
	void create_event() {
		Member member2 = Member.builder()
				.username("isd")
				.password("12345678")
				.build();

		em.persist(member2);
		em.flush();
		em.clear();

		ContestCreateRequest request = ContestCreateRequest.builder()
				.username("isd")
				.eventCategory("ffdsdf")
				.imageUrl("///")
				.title("대회")
				.description("fdsfdsf")
				.startDate(LocalDate.of(2025, 9, 23))
				.deadlineDate(LocalDate.of(2025, 9, 25))
				.build();

		contestService.contestCreate(request);


	}

}

package com.project.nextmatch.service;

import com.project.nextmatch.domain.Contest;
import com.project.nextmatch.domain.Member;
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
}
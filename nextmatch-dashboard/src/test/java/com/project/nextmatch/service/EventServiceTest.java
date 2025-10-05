package com.project.nextmatch.service;

import com.project.nextmatch.domain.Event;
import com.project.nextmatch.domain.Member;
import com.project.nextmatch.repository.EventRepository;
import com.project.nextmatch.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

// Mockito를 사용하여 테스트 대상(Service)과 의존성(Repository)을 가짜 객체로 대체합니다.
@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @InjectMocks // 테스트 대상이 되는 Service 객체에 Mock 객체들을 주입합니다.
    private EventService eventService;

    @Mock // 가짜 객체로 동작할 Repository
    private EventRepository eventRepository;

    private Member testMember;

    @BeforeEach
    void setUp() {
        // 모든 테스트 전에 사용할 공통 Member 객체를 설정합니다.
        testMember = Member.builder().id(1L).username("host").password("pw").build();
    }

    // 🔴 Red 단계: 검색 기능이 아직 EventRepository와 EventService에 구현되지 않았으므로 실패해야 합니다.
    @Test
    @DisplayName("검색 키워드를 이용해 대회를 조회하면, 키워드를 포함하는 대회만 반환해야 한다.")
    void should_return_filtered_events_when_search_keyword_is_provided() {
        // Given (준비)
        String keyword = "배드민턴";

        // 검색 결과로 나올 Event 데이터 정의
        Event event1 = Event.builder()
                .id(1L).member(testMember).title("문경 배드민턴 대회").eventCategory("배드민턴")
                .eventDate(LocalDate.now()).status("UPCOMING").build();
        Event event2 = Event.builder()
                .id(2L).member(testMember).title("테니스 챔피언십").eventCategory("테니스")
                .eventDate(LocalDate.now()).status("UPCOMING").build();
        Event event3 = Event.builder()
                .id(3L).member(testMember).title("서울 배드민턴 클럽전").eventCategory("배드민턴")
                .eventDate(LocalDate.now()).status("UPCOMING").build();

        // Repository의 findByTitleContainingIgnoreCase 호출 시, event1과 event3을 반환하도록 설정
        given(eventRepository.findByTitleContainingIgnoreCase(keyword))
                .willReturn(List.of(event1, event3));

        // When (실행)
        // EventService의 getEventList 메서드가 실제로 호출됩니다.
        var resultList = eventService.getEventList();

        // Then (검증)
        // 1. 반환된 리스트의 크기는 2여야 합니다.
        assertThat(resultList).hasSize(2);

        // 2. 반환된 리스트의 첫 번째 항목 제목은 "문경 배드민턴 대회"여야 합니다.
        assertThat(resultList.get(0).getTitle()).isEqualTo("문경 배드민턴 대회");

        // 3. 반환된 리스트의 두 번째 항목 제목은 "서울 배드민턴 클럽전"여야 합니다.
        assertThat(resultList.get(1).getTitle()).isEqualTo("서울 배드민턴 클럽전");
    }
}
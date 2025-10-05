package com.project.nextmatch.service;

import com.project.nextmatch.domain.Event;
import com.project.nextmatch.domain.Member;
import com.project.nextmatch.dto.EventCreateRequest;
import com.project.nextmatch.repository.EventRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    // private final MemberRepository memberRepository;

    // --- 대회 생성 메서드 (기존과 동일) ---
    @Transactional
    public Long createEvent(EventCreateRequest request) {
        // ... (Member 객체 생성 및 Event 저장 로직은 기존과 동일)
        Member member = Member.builder().id(request.getMemberId()).username("test_user").password("").build();

        Event newEvent = Event.builder()
                .title(request.getTitle())
                .eventCategory(request.getEventCategory())
                .description(request.getDescription())
                .eventDate(request.getEventDate())
                .deadlineDate(request.getDeadlineDate())
                .imageUrl(request.getImageUrl())
                .member(member)
                .status("CREATED")
                .build();

        Event savedEvent = eventRepository.save(newEvent);
        return savedEvent.getId();
    }

    // --- 대회 목록 조회 (검색 기능 추가 및 getEventList 대체) ---
    /**
     * 대회 목록을 가져옵니다. (검색어 처리 추가)
     * @param search 검색어 (대회 제목 기준)
     * @return EventListResponse 목록
     */
    public List<EventListResponse> listEvents(String search) {
        List<Event> events;

        // 검색어 처리 로직
        if (search != null && !search.trim().isEmpty()) {
            // 검색어가 있을 경우: 제목 검색 메서드 사용
            events = eventRepository.findByTitleContainingWithMember(search);
        } else {
            // 검색어가 없을 경우: 전체 목록 조회 메서드 사용
            events = eventRepository.findAllWithMember();
        }

        // 트랜잭션 범위 안에서 DTO로 변환하여 반환합니다.
        return events.stream()
                .map(EventListResponse::new)
                .collect(Collectors.toList());
    }

    // --- EventListResponse DTO 정의 (기존과 동일하며, 오류 해결된 형태) ---
    @Getter
    public static class EventListResponse {
        private final Long id;
        private final String title;
        private final String eventCategory;
        private final LocalDate eventDate;
        private final String memberUsername; // 필드명을 'memberUsername'으로 명확히 변경

        public EventListResponse(Event event) {
            this.id = event.getId();
            this.title = event.getTitle();
            this.eventCategory = event.getEventCategory();
            this.eventDate = event.getEventDate();

            // 이전에 컴파일 오류가 났던 부분을 해결했습니다. (Member 엔티티의 username 필드 사용)
            this.memberUsername = event.getMember().getUsername();
        }
    }
}
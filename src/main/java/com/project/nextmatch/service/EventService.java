package com.project.nextmatch.service;

import com.project.nextmatch.domain.Event;
import com.project.nextmatch.domain.Member;
import com.project.nextmatch.dto.EventCreateRequest;
import com.project.nextmatch.repository.EventRepository;
import com.project.nextmatch.dto.EventListResponse;
import com.project.nextmatch.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
@Transactional
@Getter
@RequiredArgsConstructor
public class EventService {

    private final MemberRepository memberRepository;
    private final EventRepository eventRepository;

    public void eventCreate(EventCreateRequest request) {
        Member member = memberRepository.findByUsername(request.getUsername()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        //대회 생성
        Event event = Event.builder()
                .member(member)
                .eventCategory(request.getEventCategory())
                .imageUrl(request.getImageUrl())
                .description(request.getDescription())
                .eventDate(request.getEventDate())
                .deadlineDate(request.getDeadlineDate())
                .build();

        eventRepository.save(event);



    };
    public List<EventListResponse> listEvents(String search) {
        List<Event> events;

        if (search != null && !search.trim().isEmpty()) {
            events = eventRepository.findByTitleContainingWithMember(search); // 🛑 Repo 메서드 호출
        } else {
            events = eventRepository.findAllWithMember(); // 🛑 Repo 메서드 호출
        }

        // EventListResponse DTO로 변환
        return events.stream()
                .map(EventListResponse::new)
                .collect(Collectors.toList());

    }
}

// 이병철
package com.project.nextmatch.service;

import com.project.nextmatch.domain.Event;
import com.project.nextmatch.dto.EventListResponse;
import com.project.nextmatch.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventListService {

    private final EventRepository eventRepository; // <--- 이 객체 주입

    public List<EventListResponse> listEvents(String search) {
        List<Event> events;

        if (search != null && !search.trim().isEmpty()) {
            events = eventRepository.findByTitleContainingWithMember(search);
        } else {
            events = eventRepository.findAllWithMember();
        }

        return events.stream()
                .map(EventListResponse::new)
                .collect(Collectors.toList());
    }
}
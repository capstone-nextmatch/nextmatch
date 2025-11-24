// 이병철
package com.project.nextmatch.service;

import com.project.nextmatch.domain.Contest;
import com.project.nextmatch.dto.ContestListResponse;
import com.project.nextmatch.repository.ContestListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContestListService {

    private final ContestListRepository contestListRepository; // <--- 이 객체 주입

    public List<ContestListResponse> listEvents(String search) {
        List<Contest> events;

        if (search != null && !search.trim().isEmpty()) {
            events = contestListRepository.findByTitleContainingWithMember(search);
        } else {
            events = contestListRepository.findAllWithMember();
        }

        return events.stream()
                .map(ContestListResponse::new)
                .collect(Collectors.toList());
    }
}

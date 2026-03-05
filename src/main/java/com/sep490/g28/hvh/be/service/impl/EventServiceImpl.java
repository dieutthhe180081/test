package com.sep490.g28.hvh.be.service.impl;

import com.sep490.g28.hvh.be.dto.event.response.EventFeedResponse;
import com.sep490.g28.hvh.be.dto.event.response.EventSimpleResponse;
import com.sep490.g28.hvh.be.entity.Event;
import com.sep490.g28.hvh.be.exception.AppException;
import com.sep490.g28.hvh.be.integration.storage.StorageService;
import com.sep490.g28.hvh.be.repository.EventRepository;
import com.sep490.g28.hvh.be.service.EventService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventServiceImpl implements EventService {

    EventRepository eventRepository;
    StorageService storageService;

    @Override
    public EventFeedResponse getEventFeeds(int pageNumber, int pageSize, boolean refresh,
                                           String name, String address, LocalDate startDate,
                                           LocalDate endDate, List<String> activitySubDomains) {

        Pageable pageable = PageRequest.of(
                pageNumber,
                pageSize,
                Sort.by(Sort.Direction.ASC, "createdAt")
        );

        Slice<Event> slice;

        if (refresh) {
            OffsetDateTime oneHourAgo = OffsetDateTime.now().minusHours(1);
            slice = eventRepository.refresh(name, address, startDate, endDate, activitySubDomains, oneHourAgo, pageable);
        } else {
            slice = eventRepository.search(name, address, startDate, endDate, activitySubDomains, pageable);
        }

        List<EventSimpleResponse> eventSimpleResponseList = Optional.of(slice.getContent())
                .map(list -> list.stream().map(e -> {

                            String firstEventImageUrl = null;

                            if (e.getImages() != null) {

                                String[] eventImages = e.getImages().split(",");
                                List<String> eventImageList = new ArrayList<>(Arrays.asList(eventImages));

                                CompletableFuture<String> firstEventImageFuture =
                                        storageService.getSignedUrlAsync(eventImageList.getFirst());

                                try {
                                    CompletableFuture.allOf(firstEventImageFuture).join();
                                    firstEventImageUrl = firstEventImageFuture.join();
                                } catch (CompletionException ex) {
                                    Throwable cause = ex.getCause();
                                    if (cause instanceof AppException ae) {

                                    } else {
                                        throw cause instanceof RuntimeException re ? re : ex;
                                    }
                                }
                            }

                            return new EventSimpleResponse(
                                    e.getOrganization().getName(),
                                    e.getName(),
                                    firstEventImageUrl,
                                    e.getAddress(),
                                    e.getStartDate(),
                                    e.getRecruitmentEndDate()
                            );
                        }

                ).toList())
                .orElse(Collections.emptyList());

        return new EventFeedResponse(
                eventSimpleResponseList,
                slice.hasNext() ? String.valueOf(pageNumber + 1) : null,
                slice.hasNext()
        );
    }
}

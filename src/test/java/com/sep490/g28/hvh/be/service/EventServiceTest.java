package com.sep490.g28.hvh.be.service;

import com.sep490.g28.hvh.be.dto.event.response.EventFeedResponse;
import com.sep490.g28.hvh.be.entity.Event;
import com.sep490.g28.hvh.be.entity.Organization;
import com.sep490.g28.hvh.be.integration.storage.StorageService;
import com.sep490.g28.hvh.be.repository.EventRepository;
import com.sep490.g28.hvh.be.service.impl.EventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    EventRepository eventRepository;

    @Mock
    StorageService storageService;

    EventServiceImpl eventService;

    @BeforeEach
    void setup() {
        eventService = new EventServiceImpl(
                eventRepository,
                storageService
        );
    }

    private Event mockEvent() {
        Event e = new Event();

        Organization org = new Organization();
        org.setName("Test Organization");

        e.setOrganization(org);
        e.setName("Event A");
        e.setAddress("Hanoi");
        e.setImages("img1 img2");
        e.setStartDate(LocalDate.now());
        e.setRecruitmentEndDate(LocalDate.now().plusDays(5));

        return e;
    }

    // ==== registerOrganization ===================================
    // ===== TC1 =====
    @Test
    void getEventFeeds_search_success() {

        Event event = mockEvent();

        Slice<Event> slice = new SliceImpl<>(List.of(event));

        when(eventRepository.search(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(Pageable.class)
        )).thenReturn(slice);

        when(storageService.getSignedUrlAsync("img1"))
                .thenReturn(CompletableFuture.completedFuture("signed-url"));

        EventFeedResponse response = eventService.getEventFeeds(
                0,
                10,
                false,
                "Sự kiện",
                "Hà Nội",
                LocalDate.of(2026, 3, 5),
                LocalDate.of(2026, 3, 10),
                List.of(Short.valueOf("20"), Short.valueOf("30"))
        );

        assertEquals(1, response.getEvents().size());
        assertEquals("signed-url", response.getEvents().getFirst().getImageUrl());

        verify(eventRepository).search(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(Pageable.class)
        );
    }

    // ===== TC2 =====
    @Test
    void getEventFeeds_refresh_success() {

        Event event = mockEvent();

        Slice<Event> slice = new SliceImpl<>(List.of(event));

        when(eventRepository.refresh(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(Pageable.class)
        )).thenReturn(slice);

        when(storageService.getSignedUrlAsync("img1"))
                .thenReturn(CompletableFuture.completedFuture("signed-url"));

        EventFeedResponse response = eventService.getEventFeeds(
                0,
                10,
                true,
                null,
                null,
                null,
                null,
                null
        );

        assertEquals(1, response.getEvents().size());

        verify(eventRepository).refresh(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(Pageable.class)
        );
    }

    // ===== TC3 =====
    @Test
    void getEventFeeds_event_without_images() {

        Event event = mockEvent();
        event.setImages(null);

        Slice<Event> slice = new SliceImpl<>(List.of(event));

        when(eventRepository.search(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(Pageable.class)
        )).thenReturn(slice);

        EventFeedResponse response = eventService.getEventFeeds(
                0,
                10,
                false,
                null,
                null,
                null,
                null,
                null
        );

        assertNull(response.getEvents().getFirst().getImageUrl());
    }


    // ===== TC4 =====
    @Test
    void getEventFeeds_should_return_null_next_page_when_no_next_slice() {

        Event event = mockEvent();

        Slice<Event> slice = new SliceImpl<>(List.of(event), PageRequest.of(0,10), false);

        when(eventRepository.search(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(Pageable.class)
        )).thenReturn(slice);

        when(storageService.getSignedUrlAsync("img1"))
                .thenReturn(CompletableFuture.completedFuture("signed-url"));

        EventFeedResponse response = eventService.getEventFeeds(
                0,
                10,
                false,
                null,
                null,
                null,
                null,
                null
        );

        assertFalse(response.isHasMore());
        assertNull(response.getNextCursor());
    }
}

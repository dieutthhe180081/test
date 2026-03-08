package com.sep490.g28.hvh.be.service;

import com.sep490.g28.hvh.be.constant.EEventStatus;
import com.sep490.g28.hvh.be.constant.EServedTarget;
import com.sep490.g28.hvh.be.constant.EServingPlaceType;
import com.sep490.g28.hvh.be.dto.event.response.EventDetailsResponse;
import com.sep490.g28.hvh.be.dto.event.response.EventFeedResponse;
import com.sep490.g28.hvh.be.entity.*;
import com.sep490.g28.hvh.be.exception.AppException;
import com.sep490.g28.hvh.be.exception.errorCodeImpl.AppCommonErrorCode;
import com.sep490.g28.hvh.be.exception.errorCodeImpl.EventErrorCode;
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
import java.util.Optional;
import java.util.UUID;
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

    UUID eventId;

    @BeforeEach
    void setup() {
        eventService = new EventServiceImpl(
                eventRepository,
                storageService
        );
        eventId = UUID.randomUUID();
    }

    private Event mockEvent() {

        Event event = new Event();
        event.setId(eventId);
        event.setName("Charity Event");
        event.setDescription("Helping people");
        event.setAddress("Hanoi");
        event.setExpectedVolAmount(10);
        event.setExpectedSerAmount(20);
        event.setServedTarget(EServedTarget.CHILDREN);
        event.setServingPlaceType(EServingPlaceType.CEMETERY);
        event.setStartDate(LocalDate.now());
        event.setEndDate(LocalDate.now().plusDays(1));
        event.setRecruitmentEndDate(LocalDate.now().minusDays(1));
        event.setStatus(EEventStatus.RECRUITING);

        Organization org = new Organization();
        org.setName("Volunteer Org");
        event.setOrganization(org);

        Host host = new Host();
        host.setPhone("0901234567");
        event.setHost(host);

        ActivitySubDomain subDomain = new ActivitySubDomain();
        subDomain.setName("Education");
        event.setActivitySubDomain(subDomain);

        EventImage img1 = new EventImage();
        img1.setImagePath("img1");

        EventImage img2 = new EventImage();
        img2.setImagePath("img2");

        event.setImages(List.of(img1, img2));

        return event;
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

    // ==== getEventDetails ===================================
    // ===== TC1 =====
    @Test
    void getEventDetails_success() {

        Event event = mockEvent();

        when(eventRepository.findById(eventId))
                .thenReturn(Optional.of(event));

        when(storageService.getSignedUrlAsync("img1"))
                .thenReturn(CompletableFuture.completedFuture("url1"));

        when(storageService.getSignedUrlAsync("img2"))
                .thenReturn(CompletableFuture.completedFuture("url2"));

        EventDetailsResponse response = eventService.getEventDetails(eventId);

        assertEquals(eventId, response.getId());
        assertEquals("Charity Event", response.getName());
        assertEquals(2, response.getImageUrls().size());
        assertEquals("Volunteer Org", response.getOrgName());
        assertEquals("0901234567", response.getHostPhone());
        assertEquals("Education", response.getActivitySubDomain());

        verify(eventRepository).findById(eventId);
    }

    // ===== TC2 =====
    @Test
    void getEventDetails_event_not_exist() {

        when(eventRepository.findById(eventId))
                .thenReturn(Optional.empty());

        AppException ex = assertThrows(
                AppException.class,
                () -> eventService.getEventDetails(eventId)
        );

        assertEquals(EventErrorCode.EVENT_NOT_EXISTED.getCode(), ex.getCode());

        verify(eventRepository).findById(eventId);
    }

    // ===== TC3 =====
    @Test
    void getEventDetails_event_without_images() {

        Event event = mockEvent();
        event.setImages(null);

        when(eventRepository.findById(eventId))
                .thenReturn(Optional.of(event));

        EventDetailsResponse response = eventService.getEventDetails(eventId);

        assertTrue(response.getImageUrls().isEmpty());
    }

    // ===== TC4 =====
    @Test
    void getEventDetails_null_host_and_org() {

        Event event = mockEvent();

        event.setHost(null);
        event.setOrganization(null);
        event.setActivitySubDomain(null);

        when(eventRepository.findById(eventId))
                .thenReturn(Optional.of(event));

        when(storageService.getSignedUrlAsync(any()))
                .thenReturn(CompletableFuture.completedFuture("url"));

        EventDetailsResponse response = eventService.getEventDetails(eventId);

        assertEquals("", response.getHostPhone());
        assertEquals("", response.getOrgName());
        assertEquals("", response.getActivitySubDomain());
    }
}

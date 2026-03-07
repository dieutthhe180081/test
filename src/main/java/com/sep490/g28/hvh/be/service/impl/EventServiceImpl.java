package com.sep490.g28.hvh.be.service.impl;

import com.sep490.g28.hvh.be.dto.event.response.EventDetailsResponse;
import com.sep490.g28.hvh.be.dto.event.response.EventFeedResponse;
import com.sep490.g28.hvh.be.dto.event.response.EventSimpleResponse;
import com.sep490.g28.hvh.be.entity.Event;
import com.sep490.g28.hvh.be.exception.AppException;
import com.sep490.g28.hvh.be.exception.errorCodeImpl.EventErrorCode;
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
                                           LocalDate endDate, List<Short> activitySubDomains) {

        Pageable pageable = PageRequest.of(
                pageNumber,
                pageSize,
                Sort.by(Sort.Direction.ASC, "created_at")
        );

        //Get the slice based on the current action is refresh (swipe up) or load more (scroll end)
        Slice<Event> slice;

        //If the action is refresh, get the slice within 1 hour ago
        if (refresh) {
            OffsetDateTime oneHourAgo = OffsetDateTime.now().minusHours(1);
            slice = eventRepository.refresh(name, address, startDate, endDate, activitySubDomains, oneHourAgo, pageable);
        //Else if the action is load more, keep getting the slice with current searching params
        } else {
            slice = eventRepository.search(name, address, startDate, endDate, activitySubDomains, pageable);
        }

        //map the slice content (list of events) to EventSimpleResponse
        List<EventSimpleResponse> eventSimpleResponseList = Optional.of(slice.getContent())
                .map(list -> list.stream().map(e -> {

                            String firstEventImageUrl = null;

                            //get signed URL of file
                            if (e.getImages() != null) {

                                String[] eventImages = e.getImages().split("\\s+");
                                List<String> eventImageList = new ArrayList<>(Arrays.asList(eventImages));

                                CompletableFuture<String> firstEventImageFuture =
                                        storageService.getSignedUrlAsync(eventImageList.getFirst());

                                try {
                                    CompletableFuture.allOf(firstEventImageFuture).join();
                                    firstEventImageUrl = firstEventImageFuture.join();
                                } catch (CompletionException ex) {
                                    Throwable cause = ex.getCause();
                                    if (cause instanceof AppException ae) {
                                        //todo: handle app exception in viewEventFeeds
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

        // If after load the slice with n size,
        // and slice.hasNext() is true (the slice will auto check this)
        // , move the cursor to the next page, which will load more content of the slice
        // (equivalent to call the api one more time)
        return new EventFeedResponse(
                eventSimpleResponseList,
                slice.hasNext() ? String.valueOf(pageNumber + 1) : null,
                slice.hasNext()
        );
    }

    @Override
    public EventDetailsResponse getEventDetails(UUID id) {
        //check id exist
        Event event = eventRepository.findById(id).orElseThrow(
                () -> new AppException(EventErrorCode.EVENT_NOT_EXISTED)
        );

        //get signed URL of file
        List<CompletableFuture<String>> imagesFutures = new ArrayList<>();
        if(event.getImages() != null) {
            String[] images = event.getImages().split("\\s+");
            List<String> imagesList = new ArrayList<>(Arrays.asList(images));
            for (String image : imagesList) {
                CompletableFuture<String> imageFuture =
                        storageService.getSignedUrlAsync(image);
                imagesFutures.add(imageFuture);
            }
        }

        List<String> imagesUrls = new ArrayList<>();
        try {

            CompletableFuture.allOf(imagesFutures.toArray(new CompletableFuture[0])).join();
            for (CompletableFuture<String> otherEvidenceFuture : imagesFutures) {
                imagesUrls.add(otherEvidenceFuture.join());
            }

        } catch (CompletionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof AppException ae) {
                //todo: handle exception at getEventDetails
            } else {
                throw cause instanceof RuntimeException re ? re : e;
            }
        }

        String hostPhone = "";
        if(event.getHost() != null) {
            hostPhone = event.getHost().getPhone();
        }

        String orgName = "";
        if(event.getOrganization() != null) {
            orgName = event.getOrganization().getName();
        }

        String activitySubDomainName = "";
        if(event.getActivitySubDomain() != null) {
            activitySubDomainName = event.getActivitySubDomain().getName();
        }

        return EventDetailsResponse.builder()
                .id(event.getId())
                .name(event.getName())
                .imageUrls(imagesUrls)
                .description(event.getDescription())
                .address(event.getAddress())
                .activitySubDomain(activitySubDomainName)
                .expectedVolAmount(event.getExpectedVolAmount())
                .expectedSerAmount(event.getExpectedSerAmount())
                .servedTarget(event.getServedTarget())
                .servingPlaceType(event.getServingPlaceType())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .recruitmentEndDate(event.getRecruitmentEndDate())
                .hostPhone(hostPhone)
                .orgName(orgName)
                .build();
    }
}

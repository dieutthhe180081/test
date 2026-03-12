package com.sep490.g28.hvh.be.service.impl;

import com.sep490.g28.hvh.be.constant.EEventStatus;
import com.sep490.g28.hvh.be.dto.event.request.EditEventRequest;
import com.sep490.g28.hvh.be.dto.event.response.EditEventResponse;
import com.sep490.g28.hvh.be.dto.event.request.SaveEventRequest;
import com.sep490.g28.hvh.be.dto.event.response.EventDetailsResponse;
import com.sep490.g28.hvh.be.dto.event.response.EventFeedResponse;
import com.sep490.g28.hvh.be.dto.event.response.EventSessionDetailsResponse;
import com.sep490.g28.hvh.be.dto.event.response.EventSimpleResponse;
import com.sep490.g28.hvh.be.entity.*;
import com.sep490.g28.hvh.be.auth.CurrentUserProvider;
import com.sep490.g28.hvh.be.exception.AppException;
import com.sep490.g28.hvh.be.exception.errorCodeImpl.ActivityDomainErrorCode;
import com.sep490.g28.hvh.be.exception.errorCodeImpl.EventErrorCode;
import com.sep490.g28.hvh.be.exception.errorCodeImpl.VolunteerErrorCode;
import com.sep490.g28.hvh.be.integration.storage.StorageService;
import com.sep490.g28.hvh.be.repository.EventRepository;
import com.sep490.g28.hvh.be.repository.*;
import com.sep490.g28.hvh.be.service.EventSessionService;
import com.sep490.g28.hvh.be.service.EventImageService;
import com.sep490.g28.hvh.be.service.EventService;
import com.sep490.g28.hvh.be.service.NotificationService;
import com.sep490.g28.hvh.be.util.GeoUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventServiceImpl implements EventService {
    private final HostRepository hostRepository;
    private final ActivitySubDomainRepository activitySubDomainRepository;
    private final EventRepository eventRepository;

    StorageService storageService;

    private final EventImageService eventImageService;
    private final EventSessionService eventSessionService;
    private final NotificationService notificationService;

    private final CurrentUserProvider currentUserProvider;
    VolunteerRepository volunteerRepository;
    VolunteerSavedEventRepository volunteerSavedEventRepository;

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

    /*
        draft -> create
               -> edit
        submit -> create
                -> edit
         */

    @Transactional
    public EditEventResponse draftEvent(EditEventRequest request) {
        if (request.getEventId() != null) {
            //event has been saved as drafted
            //get event from db
            Event event = eventRepository.findById(request.getEventId()).orElseThrow(
                    () -> new AppException(EventErrorCode.EVENT_NOT_FOUND)
            );
            return editEvent(request, event, EEventStatus.EDITING);
        } else {
            return createEvent(request, EEventStatus.EDITING);
        }
    }

    @Transactional
    public EditEventResponse submitEvent(EditEventRequest request) {
        if (request.getEventId() != null) {
            //event has been saved as drafted
            //get event from db
            Event event = eventRepository.findById(request.getEventId()).orElseThrow(
                    () -> new AppException(EventErrorCode.EVENT_NOT_FOUND)
            );
            return editEvent(request, event, EEventStatus.SUMMITED);
        } else {
            return createEvent(request, EEventStatus.SUMMITED);
        }
    }

    private EditEventResponse createEvent(EditEventRequest request, EEventStatus eventStatus) {

        Event event = new Event();
        EditEventResponse response = new EditEventResponse();
        //the event is completely new

        ActivitySubDomain activitySubDomain = activitySubDomainRepository.findById(request.getActivitySubDomainId())
                .orElseThrow(() -> new AppException(ActivityDomainErrorCode.SUBDOMAIN_NOT_EXISTED));
        event.setActivitySubDomain(activitySubDomain);

        ActivityDomain activityDomain = activitySubDomain.getActivityDomain();
        Short sessionMaxTime = activityDomain.getSpecialSessionMaxTime() == null ? 4 : activityDomain.getSpecialSessionMaxTime();
        eventSessionService.addEventSessionsForCreateEvent(
                event,
                request.getRecruitmentEndDate(),
                request.getEventSessions(),
                sessionMaxTime
        );

        //set event's information
        mapEventSimpleField(request, event);

        event.setStatus(eventStatus);
        //save event
        event = eventRepository.save(event);

        //adding images
        List<String> uploadUrls = eventImageService.addEventImages(event, request.getUpdateImages());
        response.setUploadUrls(uploadUrls);

        //after finish all things to do with repo or other services, send notification to host if the event is submitted
        if (eventStatus.equals(EEventStatus.SUMMITED)) {
            notificationService.sendEventCreatedNotification(event, event.getHost());
        }
        return response;
    }

    private EditEventResponse editEvent(EditEventRequest request, Event event, EEventStatus eventStatus) {
        EditEventResponse response = new EditEventResponse();
        if (!EEventStatus.editable(event.getStatus()))
            //event is not edit table
            throw new AppException(EventErrorCode.EVENT_NOT_EDITABLE);

        //event ís editable
        //edit event's images
        List<String> uploadUrls = eventImageService.updateEventImages(event, request.getUpdateImages());
        response.setUploadUrls(uploadUrls);

        //edit EventDateTime
        ActivitySubDomain activitySubDomain = activitySubDomainRepository.findById(request.getActivitySubDomainId())
                .orElseThrow(() -> new AppException(ActivityDomainErrorCode.SUBDOMAIN_NOT_EXISTED));
        event.setActivitySubDomain(activitySubDomain);

        ActivityDomain activityDomain = activitySubDomain.getActivityDomain();
        Short sessionMaxTime =
                activityDomain.getSpecialSessionMaxTime() == null
                        ? 4
                        : activityDomain.getSpecialSessionMaxTime();
        eventSessionService.updateEventSessions(
                event,
                request.getRecruitmentEndDate(),
                request.getEventSessions(),
                sessionMaxTime
        );

        //set event's information
        mapEventSimpleField(request, event);
        event.setStatus(eventStatus);

        //save event
        eventRepository.save(event);

        //after finish all things to do with repo or other services, send notification to host if the event is submitted
        if (eventStatus.equals(EEventStatus.SUMMITED)) {
            notificationService.sendEventCreatedNotification(event, event.getHost());
        }
        return response;
    }

    private void mapEventSimpleField(EditEventRequest request, Event event) {
        Host host = hostRepository.getReferenceById(currentUserProvider.getId());
        Organization organization = host.getOrganization();

        event.setHost(host);
        event.setCreateBy(host);
        event.setOrganization(organization);

        //check in place
        Point checkInLocation = GeoUtils.toPoint(request.getCheckInPlaceLat(), request.getCheckInPlaceLng());
        event.setCheckInLocation(checkInLocation);
        event.setCheckInAccuracyMeters((double) request.getCheckInPlaceAccuracyMeters());

        event.setName(request.getName());
        event.setDescription(request.getDescription());
        event.setAddress(request.getAddress());

        event.setAutoApprove(request.getAutoApprove());
        event.setServedTarget(request.getServedTarget());
        event.setServingPlaceType(request.getServingPlaceType());

        event.setRecruitmentEndDate(request.getRecruitmentEndDate());
    }

    @Override
    public EventDetailsResponse getEventDetails(UUID id) {
        //check id exist
        Event event = eventRepository.findById(id).orElseThrow(
                () -> new AppException(EventErrorCode.EVENT_NOT_EXISTED)
        );

        //get signed URL of file
        List<CompletableFuture<String>> imagesFutures = new ArrayList<>();
        if (event.getImages() != null) {
            List<EventImage> imagesList = event.getImages();
            for (EventImage image : imagesList) {
                CompletableFuture<String> imageFuture =
                        storageService.getSignedUrlAsync(image.getImagePath());
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
        if (event.getHost() != null) {
            hostPhone = event.getHost().getPhone();
        }

        String orgName = "";
        if (event.getOrganization() != null) {
            orgName = event.getOrganization().getName();
        }

        String activitySubDomainName = "";
        if (event.getActivitySubDomain() != null) {
            activitySubDomainName = event.getActivitySubDomain().getName();
        }

        //Map event sessions to response
        List<EventSessionDetailsResponse> eventSessions = event.getDateTimes().stream()
                .map(es -> new EventSessionDetailsResponse(
                        es.getId(),
                        es.getStartDateTime(),
                        es.getEndDateTime(),
                        es.getExpectedVolAmount(),
                        es.getExpectedSerAmount()
                )).toList();

        return EventDetailsResponse.builder()
                .id(event.getId())
                .name(event.getName())
                .imageUrls(imagesUrls)
                .description(event.getDescription())
                .address(event.getAddress())
                .activitySubDomain(activitySubDomainName)
                .servedTarget(event.getServedTarget())
                .servingPlaceType(event.getServingPlaceType())
                .startDate(event.getStartDate())
                .recruitmentEndDate(event.getRecruitmentEndDate())
                .hostPhone(hostPhone)
                .orgName(orgName)
                .eventSessions(eventSessions)
                .build();
    }

    @Override
    public void saveEvent(SaveEventRequest request) {

        UUID volunteerId = currentUserProvider.getId();
        UUID eventId = UUID.fromString(request.getEventId());


        //get volunteer from id
        Volunteer volunteer = volunteerRepository.findById(volunteerId).orElseThrow(
                () -> new AppException(VolunteerErrorCode.VOLUNTEER_NOT_EXISTED)
        );


        //get event from id
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new AppException(EventErrorCode.EVENT_NOT_EXISTED)
        );

        //create volunteerSavedEvent in db
        VolunteerSavedEvent volunteerSavedEvent = new VolunteerSavedEvent();
        volunteerSavedEvent.setVolunteer(volunteer);
        volunteerSavedEvent.setEvent(event);

        volunteerSavedEventRepository.save(volunteerSavedEvent);
    }

}


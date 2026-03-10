package com.sep490.g28.hvh.be.service.impl;

import com.sep490.g28.hvh.be.auth.CurrentUserProvider;
import com.sep490.g28.hvh.be.constant.EEventStatus;
import com.sep490.g28.hvh.be.dto.event.response.EditEventResponse;
import com.sep490.g28.hvh.be.dto.event.request.EditEventRequest;
import com.sep490.g28.hvh.be.entity.*;
import com.sep490.g28.hvh.be.exception.AppException;
import com.sep490.g28.hvh.be.exception.errorCodeImpl.ActivityDomainErrorCode;
import com.sep490.g28.hvh.be.exception.errorCodeImpl.EventErrorCode;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventServiceImpl implements EventService {
    private final HostRepository hostRepository;
    private final ActivitySubDomainRepository activitySubDomainRepository;
    private final EventRepository eventRepository;

    private final CurrentUserProvider currentUserProvider;

    private final EventImageService eventImageService;
    private final EventSessionService eventSessionService;
    private final NotificationService notificationService;

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

        //todo, còn phải check event time nữa, tách ra update và create riêng
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
        notificationService.sendEventCreatedNotification(event, event.getHost());
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

}

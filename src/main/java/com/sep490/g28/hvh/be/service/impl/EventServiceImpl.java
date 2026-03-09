package com.sep490.g28.hvh.be.service.impl;

import com.sep490.g28.hvh.be.auth.CurrentUserProvider;
import com.sep490.g28.hvh.be.constant.EEventStatus;
import com.sep490.g28.hvh.be.constant.EUpdateAction;
import com.sep490.g28.hvh.be.dto.checkinplace.request.EditCheckInPlaceRequest;
import com.sep490.g28.hvh.be.dto.event.response.EditEventResponse;
import com.sep490.g28.hvh.be.dto.event.request.EditEventRequest;
import com.sep490.g28.hvh.be.dto.eventimage.request.EditEventImageRequest;
import com.sep490.g28.hvh.be.entity.*;
import com.sep490.g28.hvh.be.exception.AppException;
import com.sep490.g28.hvh.be.exception.errorCodeImpl.ActivityDomainErrorCode;
import com.sep490.g28.hvh.be.exception.errorCodeImpl.EventErrorCode;
import com.sep490.g28.hvh.be.repository.*;
import com.sep490.g28.hvh.be.service.EventImageService;
import com.sep490.g28.hvh.be.service.EventService;
import com.sep490.g28.hvh.be.service.NotificationService;
import com.sep490.g28.hvh.be.util.GeoUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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
    private final NotificationService notificationService;

    private final CheckInPlaceRepository checkInPlaceRepository;

    private static final int MAX_IMAGES = 5;
    private static final int MAX_PLACES = 10;

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
        //check request: valid add image amount?
        int countAddImages = (int) request.getUpdateImages().stream()
                .filter(r -> r.getUpdateAction() == EUpdateAction.ADD)
                .count();
        if (countAddImages > MAX_IMAGES) {
            throw new AppException(EventErrorCode.INVALID_IMAGES_AMOUNT);
        }

        //check request: valid add place amount?
        int countAddPlaces = (int) request.getCheckInPlaces().stream()
                .filter(r -> r.getUpdateAction() == EUpdateAction.ADD)
                .count();
        if (countAddPlaces < 1 || countAddPlaces > MAX_PLACES) {
            throw new AppException(EventErrorCode.INVALID_CHECKIN_PLACES_AMOUNT);
        }

        //set event's information
        mapEventSimpleField(request, event);
        event.setStatus(eventStatus);

        //save event
        event = eventRepository.save(event);

        //adding images
        List<String> uploadUrls = eventImageService.addEventImages(event, request.getUpdateImages());
        response.setUploadUrls(uploadUrls);

        //add CheckinPlace
        addCheckInPlaces(event, request.getCheckInPlaces(), MAX_PLACES);

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
        //edit check in places
        updateCheckInPlaces(event, request.getCheckInPlaces());

        //edit event's images
        List<String> uploadUrls = eventImageService.updateEventImages(event, request.getUpdateImages());
        response.setUploadUrls(uploadUrls);

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

        ActivitySubDomain activitySubDomain = activitySubDomainRepository.findById(request.getActivitySubDomainId())
                .orElseThrow(() -> new AppException(ActivityDomainErrorCode.SUBDOMAIN_NOT_EXISTED));
        event.setActivitySubDomain(activitySubDomain);
        //todo, còn phải check event time nữa

        event.setHost(host);
        event.setCreateBy(host);
        event.setOrganization(organization);

        event.setName(request.getName());
        event.setDescription(request.getDescription());
        event.setAddress(request.getAddress());

        event.setAutoApprove(request.getAutoApprove());
        event.setExpectedVolAmount(request.getExpectedVolAmount());
        event.setExpectedSerAmount(request.getExpectedSerAmount());
        event.setServedTarget(request.getServedTarget());
        event.setServingPlaceType(request.getServingPlaceType());

        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setRecruitmentEndDate(request.getRecruitmentEndDate());

        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
    }


    private void updateCheckInPlaces(Event event, List<EditCheckInPlaceRequest> reqPlaces) {

        //request place empty, don't need to update
        if (reqPlaces == null || reqPlaces.isEmpty()) return;

        List<CheckInPlace> existingPlaces = checkInPlaceRepository.findByEventId(event.getId());

        //categorize update place request base on action
        List<EditCheckInPlaceRequest> removes = new ArrayList<>();
        List<EditCheckInPlaceRequest> edits = new ArrayList<>();
        List<EditCheckInPlaceRequest> adds = new ArrayList<>();

        for (EditCheckInPlaceRequest r : reqPlaces) {
            switch (r.getUpdateAction()) {
                case REMOVE -> removes.add(r);
                case EDIT -> edits.add(r);
                case ADD -> adds.add(r);
            }
        }

        //check the amount
        int existingCount = existingPlaces.size();
        int removeCount = removes.size();
        int addCount = adds.size();

        //the amount of check in place after update
        int finalCount = existingCount - removeCount + addCount;
        if (finalCount < 1 || finalCount > MAX_PLACES) {
            throw new AppException(EventErrorCode.INVALID_CHECKIN_PLACES_AMOUNT);
        }

        //remove
        if (!removes.isEmpty()) {
            Set<UUID> removeIds = removes.stream()
                    .map(EditCheckInPlaceRequest::getCheckInPlaceId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            checkInPlaceRepository.deleteAllById(removeIds);

            //remove deleted EventImage from Event
            event.getCheckInPlaces().removeIf(place -> removeIds.contains(place.getId()));
        }

        //edit
        if (!edits.isEmpty()) {

            //map using Id
            Map<UUID, EditCheckInPlaceRequest> editMap = edits.stream()
                    .collect(Collectors.toMap(
                            EditCheckInPlaceRequest::getCheckInPlaceId,
                            r -> r
                    ));

            //get the object that will be update out from existing ones
            List<CheckInPlace> toUpdate = existingPlaces.stream()
                    .filter(p -> editMap.containsKey(p.getId()))
                    .toList();

            //update
            for (CheckInPlace place : toUpdate) {
                EditCheckInPlaceRequest r = editMap.get(place.getId());

                place.setLocation(GeoUtils.toPoint(r.getLat(), r.getLng()));
                place.setAccuracyMeters(((double) r.getAccuracyMeters()));
            }
            checkInPlaceRepository.saveAll(toUpdate);
        }

        //add
        if (!adds.isEmpty()) {

            int remainingSlots = MAX_PLACES - (existingCount - removeCount);

            addCheckInPlaces(event, adds, remainingSlots);
        }
    }

    private void addCheckInPlaces(Event event, List<EditCheckInPlaceRequest> addRequest, int limitAddingAmount) {
        List<CheckInPlace> toAdd = addRequest.stream()
                .filter(r -> r.getUpdateAction() == EUpdateAction.ADD)
                .limit(limitAddingAmount)
                .map(r -> {
                    CheckInPlace cp = new CheckInPlace();
                    cp.setEvent(event);
                    cp.setLocation(GeoUtils.toPoint(r.getLat(), r.getLng()));
                    cp.setAccuracyMeters(((double) r.getAccuracyMeters()));
                    cp.setCreateBy(event.getCreateBy());
                    return cp;
                })
                .toList();

        checkInPlaceRepository.saveAll(toAdd);
    }
}

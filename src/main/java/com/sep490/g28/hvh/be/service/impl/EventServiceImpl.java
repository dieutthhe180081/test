package com.sep490.g28.hvh.be.service.impl;

import com.sep490.g28.hvh.be.auth.CurrentUserProvider;
import com.sep490.g28.hvh.be.constant.EEventStatus;
import com.sep490.g28.hvh.be.constant.EUpdateAction;
import com.sep490.g28.hvh.be.dto.checkinplace.request.UpdateCheckInPlaceRequest;
import com.sep490.g28.hvh.be.dto.event.response.CreateEventRequestResponse;
import com.sep490.g28.hvh.be.dto.event.request.CreateEventRequest;
import com.sep490.g28.hvh.be.dto.eventImage.request.UpdateEventImageRequest;
import com.sep490.g28.hvh.be.entity.*;
import com.sep490.g28.hvh.be.exception.AppException;
import com.sep490.g28.hvh.be.exception.errorCodeImpl.ActivityDomainErrorCode;
import com.sep490.g28.hvh.be.exception.errorCodeImpl.EventErrorCode;
import com.sep490.g28.hvh.be.integration.storage.StoragePathGenerator;
import com.sep490.g28.hvh.be.integration.storage.StorageService;
import com.sep490.g28.hvh.be.repository.*;
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
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventServiceImpl implements EventService {
    private final HostRepository hostRepository;
    private final CurrentUserProvider currentUserProvider;
    private final ActivitySubDomainRepository activitySubDomainRepository;
    private final EventRepository eventRepository;

    private final NotificationService notificationService;
    private final CheckInPlaceRepository checkInPlaceRepository;
    private final StoragePathGenerator storagePathGenerator;
    private final EventImageRepository eventImageRepository;

    private static final int MAX_IMAGES = 5;
    private static final int MAX_PLACES = 10;
    private final StorageService storageService;

    @Override
    @Transactional
    public CreateEventRequestResponse editEvent(CreateEventRequest request) {

        Event event = new Event();
        CreateEventRequestResponse response = new CreateEventRequestResponse();

        if (request.getEventId() != null) {
            //event has been saved as drafted
            //get event from db
            event = eventRepository.findById(request.getEventId()).orElseThrow(
                    () -> new AppException(EventErrorCode.EVENT_NOT_FOUND)
            );

            if (EEventStatus.editable(event.getStatus())) {
                //event ís editable
                //edit check in places
                updateCheckInPlaces(event, request.getCheckInPlaces());

                //edit event's images
                List<String> uploadUrls = updateEventImages(event, request.getUpdateImages());
                response.setUploadUrls(uploadUrls);
            } else
                //event is not edit table
                throw new AppException(EventErrorCode.EVENT_NOT_EDITABLE);
        } else {
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
        }

        //set event's information
        mapEventSimpleField(request, event);

        if (Boolean.TRUE.equals(request.getSubmit())) {
            //host confirm submit event
            event.setStatus(EEventStatus.SUMMITED);
        } else {
            //the event still is draft
            event.setStatus(EEventStatus.EDITING);
        }

        //save event
        event = eventRepository.save(event);

        //adding images
        List<String> uploadUrls = addEventImages(event, request.getUpdateImages(), MAX_IMAGES);
        response.setUploadUrls(uploadUrls);

        //add CheckinPlace
        addCheckInPlaces(event, request.getCheckInPlaces(), MAX_PLACES);

        //after finish all things to do with repo or other services, send notification to host if the event is submitted
        if (Boolean.TRUE.equals(request.getSubmit())) {
            notificationService.sendEventSubmited();
        }

        return response;
    }

    private void mapEventSimpleField(CreateEventRequest request, Event event) {
        Host host = hostRepository.getReferenceById(currentUserProvider.getId());
        Organization organization = host.getOrganization();

        ActivitySubDomain activitySubDomain = activitySubDomainRepository.findById(request.getActivitySubDomainId())
                .orElseThrow(() -> new AppException(ActivityDomainErrorCode.SUBDOMAIN_NOT_EXISTED));
        event.setActivitySubDomain(activitySubDomain);

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


    private List<String> addEventImages(Event event, List<UpdateEventImageRequest> addImages, int limitAddingAmount) {
        List<CompletableFuture<String>> urlFutures = new ArrayList<>();
        //go through add list to create object EventImage and get upload url
        List<EventImage> toAdd = addImages.stream()
                .limit(limitAddingAmount)
                .map(r -> {
                    UUID imageId = UUID.randomUUID();
                    EventImage image = new EventImage();
                    image.setId(imageId);
                    image.setEvent(event);
                    String path = storagePathGenerator.eventImage(event.getId(), imageId, r.getFileExtension());
                    image.setImagePath(path);

                    // generate upload url
                    CompletableFuture<String> urlFuture =
                            storageService.getUploadUrlAsync(path);
                    urlFutures.add(urlFuture);

                    return image;
                })
                .toList();

        eventImageRepository.saveAll(toAdd);

        //turn upload url into list
        CompletableFuture<Void> all =
                CompletableFuture.allOf(urlFutures.toArray(new CompletableFuture[0]));

        //return upload urls
        return all.thenApply(v ->
                urlFutures.stream()
                        .map(CompletableFuture::join)
                        .toList()
        ).join();
    }

    private List<String> updateEventImages(Event event, List<UpdateEventImageRequest> reqImages) {
        //request image empty, don't need to update
        if (reqImages == null || reqImages.isEmpty()) return Collections.emptyList();

        List<EventImage> existingImages = event.getImages();

        //categorize update image request base on action
        List<UpdateEventImageRequest> removes = new ArrayList<>();
        List<UpdateEventImageRequest> adds = new ArrayList<>();

        for (UpdateEventImageRequest r : reqImages) {
            if (r.getUpdateAction().equals(EUpdateAction.REMOVE)) {
                removes.add(r);
            } else
                adds.add(r);
        }

        //check the amount
        int existingCount = existingImages.size();
        int removeCount = removes.size();
        int addCount = adds.size();

        //the amount of check in place after update
        int finalCount = existingCount - removeCount + addCount;
        if (finalCount > MAX_IMAGES) {
            throw new AppException(EventErrorCode.INVALID_IMAGES_AMOUNT);
        }

        //remove
        if (!removes.isEmpty()) {
            Set<UUID> removeIds = removes.stream()
                    .map(UpdateEventImageRequest::getImageId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            eventImageRepository.deleteAllById(removeIds);
        }

        //add
        if (!adds.isEmpty()) {
            int remainingSlots = MAX_PLACES - (existingCount - removeCount);

            return addEventImages(event, adds, remainingSlots);
        }

        return Collections.emptyList();
    }



    private void updateCheckInPlaces(Event event, List<UpdateCheckInPlaceRequest> reqPlaces) {

        //request place empty, don't need to update
        if (reqPlaces == null || reqPlaces.isEmpty()) return;

        List<CheckInPlace> existingPlaces = checkInPlaceRepository.findByEventId(event.getId());

        //categorize update place request base on action
        List<UpdateCheckInPlaceRequest> removes = new ArrayList<>();
        List<UpdateCheckInPlaceRequest> edits = new ArrayList<>();
        List<UpdateCheckInPlaceRequest> adds = new ArrayList<>();

        for (UpdateCheckInPlaceRequest r : reqPlaces) {
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
                    .map(UpdateCheckInPlaceRequest::getCheckInPlaceId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            checkInPlaceRepository.deleteAllById(removeIds);
        }

        //edit
        if (!edits.isEmpty()) {

            //map using Id
            Map<UUID, UpdateCheckInPlaceRequest> editMap = edits.stream()
                    .collect(Collectors.toMap(
                            UpdateCheckInPlaceRequest::getCheckInPlaceId,
                            r -> r
                    ));

            //get the object that will be update out from existing ones
            List<CheckInPlace> toUpdate = existingPlaces.stream()
                    .filter(p -> editMap.containsKey(p.getId()))
                    .toList();

            //update
            for (CheckInPlace place : toUpdate) {
                UpdateCheckInPlaceRequest r = editMap.get(place.getId());

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

    private void addCheckInPlaces(Event event, List<UpdateCheckInPlaceRequest> addRequest, int limitAddingAmount) {
        List<CheckInPlace> toAdd = addRequest.stream()
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

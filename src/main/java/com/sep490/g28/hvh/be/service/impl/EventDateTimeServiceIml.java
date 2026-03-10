package com.sep490.g28.hvh.be.service.impl;

import com.sep490.g28.hvh.be.constant.EUpdateAction;
import com.sep490.g28.hvh.be.dto.eventdatetime.request.EditEventDateTimeRequest;
import com.sep490.g28.hvh.be.entity.Event;
import com.sep490.g28.hvh.be.entity.EventDateTime;
import com.sep490.g28.hvh.be.exception.AppException;
import com.sep490.g28.hvh.be.exception.errorCodeImpl.EventErrorCode;
import com.sep490.g28.hvh.be.repository.EventDateTimeRepository;
import com.sep490.g28.hvh.be.service.EventDateTimeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventDateTimeServiceIml implements EventDateTimeService {

    EventDateTimeRepository eventDateTimeRepository;

    @Override
    public void addEventDateTimesForCreateEvent(
            Event event,
            LocalDate recruitmentEndDate,
            List<EditEventDateTimeRequest> dateTimeRequests,
            Short sessionMaxTime
    ){
        //check request: valid add place amount?
        List<EditEventDateTimeRequest> adds = dateTimeRequests.stream()
                .filter(r -> (
                        r.getUpdateAction() == EUpdateAction.ADD
                        && Duration.between(r.getStartDateTime(), r.getEndDateTime()).toHours() <= sessionMaxTime)
                )
                .toList();

        if (adds.isEmpty()) {
            throw new AppException(EventErrorCode.INVALID_DATE_TIME_AMOUNT);
        }

        addEventDateTimes(event, adds);
    }

    private void addEventDateTimes(
            Event event,
            List<EditEventDateTimeRequest> addRequestList
    ) {
        List<EventDateTime> toAdd = addRequestList.stream()
                .map(r -> {
                    EventDateTime dt = new EventDateTime();
                    dt.setEvent(event);
                    dt.setStartDateTime(r.getStartDateTime());
                    dt.setEndDateTime(r.getEndDateTime());
                    return dt;
                })
                .toList();

        eventDateTimeRepository.saveAll(toAdd);
    }

    @Override
    public void updateEventDateTimes(
            Event event,
            LocalDate recruitmentEndDate,
            List<EditEventDateTimeRequest> dateTimeRequests,
            Short sessionMaxTime
    ) {
        //request datetime empty, don't need to update
        if (dateTimeRequests == null || dateTimeRequests.isEmpty()) return;

        List<EventDateTime> existingDateTimes = eventDateTimeRepository.findByEventId(event.getId());

        //categorize update place request base on action
        List<EditEventDateTimeRequest> removes = new ArrayList<>();
        List<EditEventDateTimeRequest> edits = new ArrayList<>();
        List<EditEventDateTimeRequest> adds = new ArrayList<>();

        for (EditEventDateTimeRequest r : dateTimeRequests) {
            if (r.getUpdateAction() == EUpdateAction.REMOVE) {
                removes.add(r);
                continue;
            }

            if (Duration.between(r.getStartDateTime(), r.getEndDateTime()).toHours() > sessionMaxTime) {
                continue;
            }

            if (r.getUpdateAction() == EUpdateAction.EDIT) {
                edits.add(r);
            } else if (r.getUpdateAction() == EUpdateAction.ADD) {
                adds.add(r);
            }
        }

        //check the amount
        int existingCount = existingDateTimes.size();
        int removeCount = removes.size();
        int addCount = adds.size();

        //the amount of check in place after update to make sure there is at least 1 date time remain after updating
        int finalCount = existingCount - removeCount + addCount;
        if (finalCount < 1) {
            throw new AppException(EventErrorCode.INVALID_DATE_TIME_AMOUNT);
        }

        //REMOVE
        if (!removes.isEmpty()) {
            Set<UUID> removeIds = removes.stream()
                    .map(EditEventDateTimeRequest::getEventDateTimeId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            eventDateTimeRepository.deleteAllById(removeIds);

            //remove deleted EventImage from Event
            event.getDateTimes().removeIf(dateTime -> removeIds.contains(dateTime.getId()));
        }

        //EDIT
        if (!edits.isEmpty()) {

            //map using Id
            Map<UUID, EditEventDateTimeRequest> editMap = edits.stream()
                    .collect(Collectors.toMap(
                            EditEventDateTimeRequest::getEventDateTimeId,
                            r -> r
                    ));

            //get the object that will be update out from existing ones
            List<EventDateTime> toUpdate = existingDateTimes.stream()
                    .filter(p -> editMap.containsKey(p.getId()))
                    .toList();

            //update
            for (EventDateTime eventDateTime : toUpdate) {
                EditEventDateTimeRequest r = editMap.get(eventDateTime.getId());

                eventDateTime.setStartDateTime(r.getStartDateTime());
                eventDateTime.setEndDateTime(r.getEndDateTime());
            }
            eventDateTimeRepository.saveAll(toUpdate);
        }

        //ADD
        if (!adds.isEmpty()) {
            addEventDateTimes(event, adds);
        }
    }
}

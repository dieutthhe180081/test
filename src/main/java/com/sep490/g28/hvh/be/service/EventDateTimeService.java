package com.sep490.g28.hvh.be.service;

import com.sep490.g28.hvh.be.dto.eventdatetime.request.EditEventDateTimeRequest;

import com.sep490.g28.hvh.be.entity.Event;

import java.time.LocalDate;
import java.util.List;

public interface EventDateTimeService {

    void addEventDateTimesForCreateEvent(
            Event event,
            LocalDate recruitmentEndDate,
            List<EditEventDateTimeRequest> dateTimeRequests,
            Short sessionMaxTime
    );

    void updateEventDateTimes(
            Event event,
            LocalDate recruitmentEndDate,
            List<EditEventDateTimeRequest> dateTimeRequests,
            Short sessionMaxTime
    );
}

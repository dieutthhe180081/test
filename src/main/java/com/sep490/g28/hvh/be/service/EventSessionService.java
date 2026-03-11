package com.sep490.g28.hvh.be.service;

import com.sep490.g28.hvh.be.dto.eventsession.request.EditEventSessionRequest;

import com.sep490.g28.hvh.be.entity.Event;

import java.time.LocalDate;
import java.util.List;

public interface EventSessionService {

    void addEventSessionsForCreateEvent(
            Event event,
            LocalDate recruitmentEndDate,
            List<EditEventSessionRequest> sessionRequests,
            Short sessionMaxTime
    );

    void updateEventSessions(
            Event event,
            LocalDate recruitmentEndDate,
            List<EditEventSessionRequest> sessionRequests,
            Short sessionMaxTime
    );
}

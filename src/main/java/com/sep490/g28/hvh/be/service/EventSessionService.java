package com.sep490.g28.hvh.be.service;

import com.sep490.g28.hvh.be.dto.eventsession.request.EditEventSessionRequest;

import com.sep490.g28.hvh.be.entity.Event;
import com.sep490.g28.hvh.be.entity.EventSession;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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

    List<EventSession> findConflictSessionDateOfHost(UUID hostId, UUID checkedEventId, List<EventSession> checkedSessions);
}

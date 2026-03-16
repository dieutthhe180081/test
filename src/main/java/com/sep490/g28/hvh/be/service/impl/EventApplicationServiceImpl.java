package com.sep490.g28.hvh.be.service.impl;

import com.sep490.g28.hvh.be.auth.CurrentUserProvider;
import com.sep490.g28.hvh.be.constant.EEventApplicationStatus;
import com.sep490.g28.hvh.be.entity.Event;
import com.sep490.g28.hvh.be.entity.EventApplication;
import com.sep490.g28.hvh.be.entity.EventSession;
import com.sep490.g28.hvh.be.exception.AppException;
import com.sep490.g28.hvh.be.exception.errorCodeImpl.EventErrorCode;
import com.sep490.g28.hvh.be.repository.EventApplicationRepository;
import com.sep490.g28.hvh.be.repository.EventRepository;
import com.sep490.g28.hvh.be.repository.EventSessionRepository;
import com.sep490.g28.hvh.be.repository.VolunteerRepository;
import com.sep490.g28.hvh.be.service.EventApplicationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventApplicationServiceImpl implements EventApplicationService {
    EventSessionRepository eventSessionRepository;
    EventApplicationRepository eventApplicationRepository;
    CurrentUserProvider currentUserProvider;
    private final EventRepository eventRepository;
    private final VolunteerRepository volunteerRepository;

    @Transactional
    @Override
    public void applyEventSession(UUID sessionId) {
        //check session existed?
        EventSession session = eventSessionRepository.findById(sessionId).orElseThrow(
                () -> new AppException(EventErrorCode.EVENT_SESSION_NOT_EXISTED));

        UUID volunteerId = currentUserProvider.getId();

        //Have ever the volunteer applied for this session yet?
        if (eventApplicationRepository.getEventApplicationsByVolunteerIdAndSessionId(volunteerId, sessionId).isPresent()) {
            throw new AppException(EventErrorCode.ALREADY_APPLIED);
        }

        //check expect Vol amount
        if (session.getExpectedVolAmount() == session.getApprovedApplicationCount()){
            throw new AppException(EventErrorCode.EVENT_SESSION_FULL);
        }

        Event event = eventRepository.findById(sessionId).orElseThrow(
                () -> new AppException(EventErrorCode.EVENT_NOT_EXISTED)
        );

        //check registration deadline
        if (LocalDate.now().isAfter(event.getRecruitmentEndDate())) {
            throw new AppException(EventErrorCode.EVENT_RECRUITMENT_CLOSED);
        }

        EventApplication eventApplication = new EventApplication();
        eventApplication.setSession(session);
        eventApplication.setVolunteer(volunteerRepository.getReferenceById(volunteerId));

        //check auto approve
        if (event.isAutoApprove()){
            eventApplication.setStatus(EEventApplicationStatus.APPROVED);
            session.setApprovedApplicationCount(session.getApprovedApplicationCount()+1);
            eventSessionRepository.save(session);
            eventApplicationRepository.save(eventApplication);
            if (session.getExpectedVolAmount() == session.getApprovedApplicationCount()){
                //todo send notification to host
            }
        } else {
            eventApplication.setStatus(EEventApplicationStatus.PENDING);
            eventApplicationRepository.save(eventApplication);
            // todo send notification to host
        }
    }
}

package com.sep490.g28.hvh.be.controller;

import com.sep490.g28.hvh.be.dto.event.request.SaveEventRequest;
import com.sep490.g28.hvh.be.dto.event.response.EventDetailsResponse;
import com.sep490.g28.hvh.be.dto.event.response.EventFeedResponse;
import com.sep490.g28.hvh.be.dto.organization.response.OrganizationRegistrationDetailsResponse;
import com.sep490.g28.hvh.be.service.EventService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/event")
@Validated
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class EventController {

    EventService eventService;

    @GetMapping("/new-feeds")
    public ResponseEntity<EventFeedResponse> getEventNewFeeds(
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "INVALID_PAGE_NUMBER") int pageNumber,
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "INVALID_PAGE_SIZE")
            @Max(value = 100, message = "INVALID_PAGE_SIZE") int pageSize,
            @RequestParam(defaultValue = "true") boolean refresh,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) List<Short> activitySubDomainIds
    ) {
        return ResponseEntity.ok(eventService.getEventFeeds(pageNumber, pageSize, refresh, name, address, startDate, endDate, activitySubDomainIds));
    }

    @GetMapping("/event-details/{id}")
    public ResponseEntity<EventDetailsResponse> getEventDetails(
            @PathVariable(name = "id") @UUID(message = "INVALID_UUID") String inputId
    ) {
        java.util.UUID id = java.util.UUID.fromString(inputId);
        return ResponseEntity.ok(eventService.getEventDetails(id));
    }

    @PostMapping("/save-event")
    public ResponseEntity<String> saveEvent(@Valid @RequestBody SaveEventRequest request) {
        eventService.saveEvent(request);
        return ResponseEntity.ok().build();
    }
}

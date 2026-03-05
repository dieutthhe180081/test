package com.sep490.g28.hvh.be.controller;

import com.sep490.g28.hvh.be.dto.event.response.EventFeedResponse;
import com.sep490.g28.hvh.be.service.EventService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/event")
@Validated
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class EventController {

    EventService eventService;

    @GetMapping("/event/new-feeds")
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
            @RequestParam(required = false) List<String> activitySubDomains
    ) {
        return ResponseEntity.ok(eventService.getEventFeeds(pageNumber, pageSize, refresh, name, address, startDate, endDate, activitySubDomains));
    }
}

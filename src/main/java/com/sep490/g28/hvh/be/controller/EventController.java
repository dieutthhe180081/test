package com.sep490.g28.hvh.be.controller;

import com.sep490.g28.hvh.be.dto.event.request.EditEventRequest;
import com.sep490.g28.hvh.be.dto.event.response.EditEventResponse;
import com.sep490.g28.hvh.be.service.EventService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @PreAuthorize("hasRole('HOST')")
    @PostMapping("/draft")
    ResponseEntity<EditEventResponse> draftEvent(@RequestBody @Valid EditEventRequest request) {

        return ResponseEntity.ok(eventService.draftEvent(request));
    }

    @PreAuthorize("hasRole('HOST')")
    @PostMapping("/submit")
    ResponseEntity<EditEventResponse> submitEvent(@RequestBody @Valid EditEventRequest request) {

        return ResponseEntity.ok(eventService.submitEvent(request));
    }
}

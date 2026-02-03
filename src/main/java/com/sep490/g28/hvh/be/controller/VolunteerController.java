package com.sep490.g28.hvh.be.controller;

import com.sep490.g28.hvh.be.dto.volunteer.RegisterVolunteerAccountRequest;
import com.sep490.g28.hvh.be.dto.volunteer.RegisterVolunteerAccountResponse;
import com.sep490.g28.hvh.be.service.VolunteerService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/volunteer")
@Validated
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class VolunteerController {

    VolunteerService volunteerService;

    @PostMapping("/register-vol-acc")
    public ResponseEntity<RegisterVolunteerAccountResponse> registerVolAccount(
            @Valid @RequestBody RegisterVolunteerAccountRequest request
    ) {
        return ResponseEntity.ok(volunteerService.registerVolAccount(request));
    }
}

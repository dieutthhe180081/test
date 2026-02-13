package com.sep490.g28.hvh.be.controller;

import com.sep490.g28.hvh.be.dto.activityDomain.CreateActivityDomainRequest;
import com.sep490.g28.hvh.be.dto.activityDomain.UpdateActivityDomainRequest;
import com.sep490.g28.hvh.be.service.ActivityDomainService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/activity-domain")
@Validated
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ActivityDomainController {

    ActivityDomainService activityDomainService;

    @PreAuthorize("hasRole('SYS_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<String> createActivityDomain(@RequestBody @Valid CreateActivityDomainRequest request) {
        activityDomainService.createActivityDomain(request);
        return ResponseEntity.ok("OK");
    }

    @PreAuthorize("hasRole('SYS_ADMIN')")
    @PostMapping("/{id}/update")
    public ResponseEntity<String> updateActivityDomain(@PathVariable(name = "id") Short inputId
            , @RequestBody @Valid UpdateActivityDomainRequest request) {
        activityDomainService.updateActivityDomain(inputId, request);
        return ResponseEntity.ok("OK");
    }
}

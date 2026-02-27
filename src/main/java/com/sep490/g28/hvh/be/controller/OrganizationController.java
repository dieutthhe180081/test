package com.sep490.g28.hvh.be.controller;

import com.sep490.g28.hvh.be.dto.organization.*;
import com.sep490.g28.hvh.be.service.OrganizationService;
import com.sep490.g28.hvh.be.validation.OrganizationRegistrationStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.UUID;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/organization")
@Validated
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class OrganizationController {

    OrganizationService organizationService;

    @PostMapping("/register-org")
    public ResponseEntity<RegisterOrganizationResponse> registerOrganization(
            @Valid @RequestBody RegisterOrganizationRequest request
    ) {
        return ResponseEntity.ok(organizationService.registerOrganization(request));
    }

    @PreAuthorize("hasRole('SYS_ADMIN')")
    @GetMapping("/registrations")
    public ResponseEntity<Page<OrganizationRegistrationSimpleResponse>> getRegistrations(
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "INVALID_PAGE_NUMBER") int pageNumber,
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "INVALID_PAGE_SIZE")
            @Max(value = 100, message = "INVALID_PAGE_SIZE") int pageSize,
            @RequestParam(required = false, name = "status") @OrganizationRegistrationStatus String status,
            @RequestParam(required = false) String managerEmail
    ) {
        return ResponseEntity.ok(organizationService.getOrgRegistrations(pageNumber, pageSize, status, managerEmail));
    }

    @PreAuthorize("hasRole('SYS_ADMIN')")
    @GetMapping("/registrations/{id}")
    public ResponseEntity<OrganizationRegistrationDetailsResponse> getRegistrationsDetails(
            @PathVariable(name = "id") @UUID(message = "INVALID_UUID") String inputId
    ) {
        java.util.UUID id = java.util.UUID.fromString(inputId);
        return ResponseEntity.ok(organizationService.getOrgRegistrationDetails(id));
    }

    @PreAuthorize("hasRole('SYS_ADMIN')")
    @PostMapping("/registrations/{id}/verify")
    public ResponseEntity<String> verifyOrgRegistration(
            @PathVariable(name = "id") @UUID(message = "INVALID_UUID") String inputId,
            @RequestBody @Valid OrganizationRegistrationVerifyRequest request
    ) {
        java.util.UUID id = java.util.UUID.fromString(inputId);
        organizationService.verifyOrgRegistration(id, request);
        return ResponseEntity.ok().build();
    }
}

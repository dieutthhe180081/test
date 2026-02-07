package com.sep490.g28.hvh.be.controller;

import com.sep490.g28.hvh.be.dto.organization.RegisterOrganizationRequest;
import com.sep490.g28.hvh.be.dto.organization.RegisterOrganizationResponse;
import com.sep490.g28.hvh.be.service.OrganizationService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

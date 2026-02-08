package com.sep490.g28.hvh.be.controller;

import com.sep490.g28.hvh.be.dto.host.CreateMultipleHostAccountRequest;
import com.sep490.g28.hvh.be.service.HostService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/host")
@Validated
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class HostController {

    HostService hostService;

    @PreAuthorize("hasRole('ORG_MANAGER')")
    @PostMapping("/create-account")
    public ResponseEntity<String> createAccount(
            @RequestBody @Valid CreateMultipleHostAccountRequest request
    ) {
        return ResponseEntity.ok(hostService.createAccount(request));
    }
}

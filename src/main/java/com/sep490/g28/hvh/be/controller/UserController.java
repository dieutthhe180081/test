package com.sep490.g28.hvh.be.controller;

import com.sep490.g28.hvh.be.dto.user.RegisterVolunteerAccountRequest;
import com.sep490.g28.hvh.be.dto.user.RegisterVolunteerAccountResponse;
import com.sep490.g28.hvh.be.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/user")
@Validated
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UserController {

    UserService userService;

    @PostMapping("/register-vol-acc")
    public ResponseEntity<RegisterVolunteerAccountResponse> registerVolAccount(
            @Valid @RequestBody RegisterVolunteerAccountRequest request
    ) {
        return ResponseEntity.ok(userService.registerVolAccount(request));
    }
}

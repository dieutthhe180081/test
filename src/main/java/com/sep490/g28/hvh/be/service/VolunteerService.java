package com.sep490.g28.hvh.be.service;

import com.sep490.g28.hvh.be.dto.volunteer.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface VolunteerService {
    RegisterVolunteerAccountResponse registerVolAccount (RegisterVolunteerAccountRequest registerVolunteerAccountRequest);

    Page<VolunteerRegistrationSimpleResponse> getRegistrations(int pageNumber, int pageSize, String inputStatus, @Email String email);

    VolunteerRegistrationDetailsResponse getRegistrationDetails(UUID id);

    VolunteerRegistrationVerifyResponse verifyRegistration(UUID id, @Valid VolunteerRegistrationVerifyRequest request);
}

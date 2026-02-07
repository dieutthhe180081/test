package com.sep490.g28.hvh.be.service;

import com.sep490.g28.hvh.be.dto.volunteer.RegisterVolunteerAccountRequest;
import com.sep490.g28.hvh.be.dto.volunteer.RegisterVolunteerAccountResponse;
import com.sep490.g28.hvh.be.dto.volunteer.VolunteerRegistrationDetailsResponse;
import com.sep490.g28.hvh.be.dto.volunteer.VolunteerRegistrationSimpleResponse;
import jakarta.validation.constraints.Email;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface VolunteerService {
    RegisterVolunteerAccountResponse registerVolAccount (RegisterVolunteerAccountRequest registerVolunteerAccountRequest);

    Page<VolunteerRegistrationSimpleResponse> getRegistrations(int pageNumber, int pageSize, String inputStatus, @Email String email);

//    VolunteerRegistrationDetailsResponse getRegistrationDetails(UUID id);
}

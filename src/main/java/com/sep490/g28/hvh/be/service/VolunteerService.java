package com.sep490.g28.hvh.be.service;

import com.sep490.g28.hvh.be.dto.volunteer.RegisterVolunteerAccountRequest;
import com.sep490.g28.hvh.be.dto.volunteer.RegisterVolunteerAccountResponse;

public interface VolunteerService {
    RegisterVolunteerAccountResponse registerVolAccount (RegisterVolunteerAccountRequest registerVolunteerAccountRequest);
}

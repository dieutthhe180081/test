package com.sep490.g28.hvh.be.service;

import com.sep490.g28.hvh.be.dto.user.RegisterVolunteerAccountRequest;
import com.sep490.g28.hvh.be.dto.user.RegisterVolunteerAccountResponse;

public interface VolunteerService {
    RegisterVolunteerAccountResponse registerVolAccount (RegisterVolunteerAccountRequest registerVolunteerAccountRequest);
}

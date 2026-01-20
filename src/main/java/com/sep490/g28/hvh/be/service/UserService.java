package com.sep490.g28.hvh.be.service;

import com.sep490.g28.hvh.be.dto.user.RegisterVolunteerAccountRequest;
import com.sep490.g28.hvh.be.dto.user.RegisterVolunteerAccountResponse;

public interface UserService {
    RegisterVolunteerAccountResponse registerVolAccount (RegisterVolunteerAccountRequest registerVolunteerAccountRequest);
}

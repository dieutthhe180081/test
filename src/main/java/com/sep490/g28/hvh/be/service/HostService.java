package com.sep490.g28.hvh.be.service;

import com.sep490.g28.hvh.be.dto.host.CreateMultipleHostAccountRequest;

public interface HostService {
    String createAccount(CreateMultipleHostAccountRequest request);
}

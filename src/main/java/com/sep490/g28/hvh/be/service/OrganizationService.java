package com.sep490.g28.hvh.be.service;

import com.sep490.g28.hvh.be.dto.organization.RegisterOrganizationRequest;
import com.sep490.g28.hvh.be.dto.organization.RegisterOrganizationResponse;

public interface OrganizationService {
    RegisterOrganizationResponse registerOrganization(RegisterOrganizationRequest registerOrganizationRequest);
}

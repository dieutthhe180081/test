package com.sep490.g28.hvh.be.service;

import com.sep490.g28.hvh.be.dto.organization.RegisterOrganizationRequest;
import com.sep490.g28.hvh.be.dto.organization.RegisterOrganizationResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrganizationServiceImpl implements OrganizationService{
    @Override
    public RegisterOrganizationResponse registerOrganization(RegisterOrganizationRequest request) {
        return null;
    }
}

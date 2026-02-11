package com.sep490.g28.hvh.be.service;

import com.sep490.g28.hvh.be.dto.activityDomain.CreateActivityDomainRequest;
import com.sep490.g28.hvh.be.dto.activityDomain.UpdateActivityDomainRequest;

import java.util.UUID;

public interface ActivityDomainService {

    void createActivityDomain(CreateActivityDomainRequest createActivityDomainRequest);

    String updateActivityDomain(Short id, UpdateActivityDomainRequest updateActivityDomainRequest);
}

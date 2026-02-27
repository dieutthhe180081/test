package com.sep490.g28.hvh.be.service;

import com.sep490.g28.hvh.be.dto.activityDomain.*;
import jakarta.validation.constraints.Email;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ActivityDomainService {

    void createActivityDomain(CreateActivityDomainRequest createActivityDomainRequest);

    String updateActivityDomain(Short id, UpdateActivityDomainRequest updateActivityDomainRequest);

    Page<ActivityDomainDetailsResponse> getActivityDomains(
            int pageNumber, int pageSize, String inputActive, String name);

    void changeActivityDomainVisibility(Short id, ChangeActivityDomainVisibilityRequest changeActivityDomainVisibilityRequest);

    void changeActivitySubDomainVisibility(Short id, ChangeActivitySubDomainVisibilityRequest changeActivitySubDomainVisibilityRequest);
}

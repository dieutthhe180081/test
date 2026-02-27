package com.sep490.g28.hvh.be.service;

import com.sep490.g28.hvh.be.dto.activityDomain.request.ChangeActivityDomainVisibilityRequest;
import com.sep490.g28.hvh.be.dto.activityDomain.request.ChangeActivitySubDomainVisibilityRequest;
import com.sep490.g28.hvh.be.dto.activityDomain.request.CreateActivityDomainRequest;
import com.sep490.g28.hvh.be.dto.activityDomain.request.UpdateActivityDomainRequest;
import com.sep490.g28.hvh.be.dto.activityDomain.response.ActivityDomainDetailsResponse;
import org.springframework.data.domain.Page;

public interface ActivityDomainService {

    void createActivityDomain(CreateActivityDomainRequest createActivityDomainRequest);

    String updateActivityDomain(Short id, UpdateActivityDomainRequest updateActivityDomainRequest);

    Page<ActivityDomainDetailsResponse> getActivityDomains(
            int pageNumber, int pageSize, String inputActive, String name);

    void changeActivityDomainVisibility(Short id, ChangeActivityDomainVisibilityRequest changeActivityDomainVisibilityRequest);

    void changeActivitySubDomainVisibility(Short id, ChangeActivitySubDomainVisibilityRequest changeActivitySubDomainVisibilityRequest);
}

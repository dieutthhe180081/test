package com.sep490.g28.hvh.be.dto.activityDomain.response;

import com.sep490.g28.hvh.be.entity.ActivitySubDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ActivitySubDomainDetailsResponse {
    private Short id;
    private String name;
    private Boolean active;

    public static ActivitySubDomainDetailsResponse from(ActivitySubDomain asd) {
        return new ActivitySubDomainDetailsResponse(
                asd.getId(),
                asd.getName(),
                asd.getActive()
        );
    }
}

package com.sep490.g28.hvh.be.dto.activityDomain;

import com.sep490.g28.hvh.be.entity.ActivityDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ActivityDomainDetailsResponse {
    private String name;
    Short specialSessionMaxTime;
    Boolean active;
    List<ActivitySubDomainDetailsResponse> activitySubDomainList;

    public static ActivityDomainDetailsResponse from(ActivityDomain ad) {
        return new ActivityDomainDetailsResponse(
                ad.getName(),
                ad.getSpecialSessionMaxTime(),
                ad.getActive(),
                ad.getActivitySubDomains().stream()
                        .map(ActivitySubDomainDetailsResponse::from).toList()
        );
    }
}

package com.sep490.g28.hvh.be.dto.organization.response;

import com.sep490.g28.hvh.be.constant.EOrgType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class OrganizationSimpleResponse {
    private UUID id;
    private String name;
    private EOrgType orgType;
    private long numberOfHostedEvents;
    //todo add rating

    public static OrganizationSimpleResponse from(Object[] row){
        return new OrganizationSimpleResponse(
                (UUID) row[0],
                (String) row[1],
                row[2] != null ? EOrgType.valueOf((String) row[2]) : null,
                Long.parseLong(row[3].toString())
        );
    }
}

package com.sep490.g28.hvh.be.dto.organization;

import com.sep490.g28.hvh.be.constant.EOrgType;
import com.sep490.g28.hvh.be.entity.OrganizationRegistration;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class OrganizationRegistrationSimpleResponse {
    private UUID id;
    private String name;
    private Boolean dhaRegistered;
    private EOrgType orgType;
    private String managerFullName;
    private String managerCid;
    private String managerEmail;

    public static OrganizationRegistrationSimpleResponse from(OrganizationRegistration or) {
        return new OrganizationRegistrationSimpleResponse(
                or.getId(),
                or.getName(),
                or.getDhaRegistered(),
                or.getOrgType(),
                or.getManagerFullName(),
                or.getManagerCid(),
                or.getManagerEmail()
        );
    }
}

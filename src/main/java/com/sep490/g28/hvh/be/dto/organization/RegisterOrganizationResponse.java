package com.sep490.g28.hvh.be.dto.organization;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RegisterOrganizationResponse {

    private String managerCidFrontUploadUrl;
    private String managerCidBackUploadUrl;
    private String managerCidHoldingUploadUrl;
    private List<String> otherEvidencesUploadUrls;
}

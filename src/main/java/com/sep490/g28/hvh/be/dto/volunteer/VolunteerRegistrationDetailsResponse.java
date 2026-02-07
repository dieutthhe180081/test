package com.sep490.g28.hvh.be.dto.volunteer;

import com.sep490.g28.hvh.be.constant.EVolunteerVerificationStatus;
import com.sep490.g28.hvh.be.entity.IdentityVerification;
import com.sep490.g28.hvh.be.entity.SystemAdmin;
import com.sep490.g28.hvh.be.entity.Volunteer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class VolunteerRegistrationDetailsResponse {
    private UUID id;
    private String cid;
    private String email;
    private String phone;
    private String cidFrontUrl;
    private String cidBackUrl;
    private String cidHoldingUrl;
    private EVolunteerVerificationStatus status;
    private String rejectionReason;
    private OffsetDateTime createdAt;
    private OffsetDateTime reviewAt;
    private SystemAdmin reviewBy;
    private Volunteer volunteer;
    private String note;

}

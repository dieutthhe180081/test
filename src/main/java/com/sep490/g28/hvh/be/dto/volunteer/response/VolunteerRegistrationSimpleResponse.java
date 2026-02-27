package com.sep490.g28.hvh.be.dto.volunteer.response;

import com.sep490.g28.hvh.be.constant.EVolunteerVerificationStatus;
import com.sep490.g28.hvh.be.entity.IdentityVerification;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class VolunteerRegistrationSimpleResponse {
    private UUID id;
    private String email;
    private String cid;
    private EVolunteerVerificationStatus status;
    private OffsetDateTime createdAt;

    public static VolunteerRegistrationSimpleResponse from(IdentityVerification iv) {
        return new VolunteerRegistrationSimpleResponse(
                iv.getId(),
                iv.getEmail(),
                iv.getCid(),
                iv.getStatus(),
                iv.getCreatedAt()
        );
    }
}

package com.sep490.g28.hvh.be.dto.organization;

import com.sep490.g28.hvh.be.validation.RequiredField;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrganizationRegistrationVerifyRequest {
    @RequiredField(fieldName = "Hành động phê duyệt tổ chức")
    Boolean approve;

    @Pattern(regexp = "^(?!\\s*$).+", message = "INVALID_REJECTION_REASON")
    String rejectionReason;
}

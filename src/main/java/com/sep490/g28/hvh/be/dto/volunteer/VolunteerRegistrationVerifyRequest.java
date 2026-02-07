package com.sep490.g28.hvh.be.dto.volunteer;

import com.sep490.g28.hvh.be.validation.RequiredField;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VolunteerRegistrationVerifyRequest {
    @RequiredField(fieldName = "Hành động phê duyệt")
    Boolean approve;

    @Pattern(regexp = "^(?!\\s*$).+", message = "REJECTION_REASON_EMPTY")
    String rejectionReason;

}

package com.sep490.g28.hvh.be.dto.volunteer;

import com.sep490.g28.hvh.be.validation.RequiredField;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VolunteerRegistrationVerifyRequest {
    @RequiredField(fieldName = "Hành động phê duyệt")
    Boolean approve;

    //null or having something
    @Pattern(regexp = "^(?!\\s*$).+", message = "INVALID_REJECTION_REASON")
    String rejectionReason;

    /*
    - start with uppercase in each word, flowing by lowercase
    - between 2 words are a space
    - not include digit, special char, space in head and tail
     */
    @Pattern(regexp = "^[A-ZÀ-Ỹ][a-zà-ỹ]*(?:\\s+[A-ZÀ-Ỹ][a-zà-ỹ]*)*$", message = "INVALID_FULL_NAME")
    @NotBlank(message = "INVALID_FULL_NAME")
    @Length(max = 100, message = "INVALID_FULL_NAME")
    String fullName;
}

package com.sep490.g28.hvh.be.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ForgotPasswordRequest {

    @NotBlank(message = "INVALID_OTP")
    @Pattern(regexp = "^\\d{6}$", message = "INVALID_OTP")
    String otp;

    @NotBlank(message = "INVALID_EMAIL")
    @Email(message = "INVALID_EMAIL")
    String email;
}

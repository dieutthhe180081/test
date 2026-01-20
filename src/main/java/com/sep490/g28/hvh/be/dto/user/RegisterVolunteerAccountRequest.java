package com.sep490.g28.hvh.be.dto.user;

import com.sep490.g28.hvh.be.validation.ImageMimeType;
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
public class RegisterVolunteerAccountRequest {
    @NotBlank(message = "INVALID_EMAIL")
    @Email(message = "INVALID_EMAIL")
    String email;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*.,:;’])[A-Za-z\\d!@#$%^&*.,:;’]{8,}$", message = "INVALID_PASSWORD")
    String password;

    @Pattern(regexp = "^(0|\\+84)(3|5|7|8|9)\\d{8}$", message = "INVALID_PHONE")
    String phone;

    @Pattern(regexp = "^\\d{12}$", message = "INVALID_CID")
    String cid;

    @NotBlank(message = "INVALID_IMAGE_TYPE")
    @ImageMimeType(fieldName = "Ảnh mặt trước căn cước công dân")
    String cidFrontMimeType;

    @NotBlank(message = "INVALID_IMAGE_TYPE")
    @ImageMimeType(fieldName = "Ảnh mặt sau căn cước công dân")
    String cidBackMimeType;

    @NotBlank(message = "INVALID_IMAGE_TYPE")
    @ImageMimeType(fieldName = "Ảnh cầm căn cước công dân")
    String cidHoldingMimeType;
}

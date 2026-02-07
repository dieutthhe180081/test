package com.sep490.g28.hvh.be.dto.organization;

import com.sep490.g28.hvh.be.validation.AllowedFileExtension;
import com.sep490.g28.hvh.be.validation.ImageFileExtension;
import com.sep490.g28.hvh.be.validation.OrgType;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterOrganizationRequest {

    @NotBlank(message = "INVALID_OTP")
    @Pattern(regexp = "^\\d{6}$", message = "INVALID_OTP")
    String otp;

    @NotBlank(message = "INVALID_ORG_NAME")
    String name;

    @NotNull(message = "INVALID_DHA_REGISTERED")
    Boolean dhaRegistered;

    @NotBlank(message = "INVALID_ORG_TYPE")
    @OrgType(message = "Organization type string format must be full uppercase and split by underscores")
    String orgType;

    @NotBlank(message = "INVALID_ORG_INTRODUCTION")
    @Length(max = 500)
    String orgIntroduction;

    @NotBlank(message = "INVALID_MANAGER_FULL_NAME")
    @Length(max = 100)
    String managerFullName;

    @NotBlank(message = "INVALID_CID")
    @Length(max = 12)
    String managerCid;

    @NotBlank(message = "INVALID_PHONE")
    @Pattern(regexp = "^(0|\\+84)(3|5|7|8|9)\\d{8}$", message = "INVALID_PHONE")
    String managerPhone;

    @NotBlank(message = "INVALID_MANAGER_EMAIL")
    @Email(message = "INVALID_EMAIL")
    String managerEmail;

    @NotBlank(message = "INVALID_IMAGE")
    @ImageFileExtension(fieldName = "Ảnh mặt trước căn cước công dân")
    String managerCidFrontExtension;

    @NotBlank(message = "INVALID_IMAGE")
    @ImageFileExtension(fieldName = "Ảnh mặt sau căn cước công dân")
    String managerCidBackExtension;

    @NotBlank(message = "INVALID_IMAGE")
    @ImageFileExtension(fieldName = "Ảnh cầm căn cước công dân")
    String managerCidHoldingExtension;

    @NotBlank(message = "INVALID_FILES")
    @AllowedFileExtension(fieldName = "Những tài liệu liên quan khác")
    String otherEvidencesExtensions;

    @NotBlank(message = "INVALID_APPLICATION_REASON")
    String applicationReason;
}

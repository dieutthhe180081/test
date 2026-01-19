package com.sep490.g28.hvh.be.dto.user;

import com.sep490.g28.hvh.be.validation.FileSizeMax;
import com.sep490.g28.hvh.be.validation.FileType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

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

    @FileSizeMax(fieldName = "Ảnh mặt trước căn cước công dân", maxFileSizeMb = 5)
    @FileType(fieldName = "Ảnh mặt trước căn cước công dân", allowedTypes = {"image/jpeg", "image/png"}, allowedTypesMessage = "image/jpeg or image/png")
    //todo check lại file type, vì một số đien thoai chup ra dinh dang khac
    MultipartFile cidFront;

    @FileSizeMax(fieldName = "Ảnh mặt sau căn cước công dân", maxFileSizeMb = 5)
    @FileType(fieldName = "Ảnh mặt sau căn cước công dân", allowedTypes = {"image/jpeg", "image/png"}, allowedTypesMessage = "image/jpeg or image/png")
    MultipartFile cidBack;

    @FileSizeMax(fieldName = "Ảnh cầm căn cước công dân", maxFileSizeMb = 5)
    @FileType(fieldName = "Ảnh cầm căn cước công dân", allowedTypes = {"image/jpeg", "image/png"}, allowedTypesMessage = "image/jpeg or image/png")
    MultipartFile holdingCidCard;
}

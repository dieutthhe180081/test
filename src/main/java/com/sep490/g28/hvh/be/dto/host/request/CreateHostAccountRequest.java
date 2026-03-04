package com.sep490.g28.hvh.be.dto.host.request;

import com.sep490.g28.hvh.be.validation.ValidAge;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateHostAccountRequest {
    @NotBlank(message = "INVALID_CID")
    @Pattern(regexp = "^\\d{12}$", message = "INVALID_CID")
    String cid;

    @NotBlank(message = "INVALID_EMAIL")
    @Email(message = "INVALID_EMAIL")
    String email;

    @NotBlank(message = "INVALID_PHONE")
    @Pattern(regexp = "^(0|\\+84)(3|5|7|8|9)\\d{8}$", message = "INVALID_PHONE")
    String phone;

    /*
    - start with uppercase in each word, flowing by lowercase
    - between 2 words are a space
    - not include digit, special char, space in head and tail
     */
    @Pattern(regexp = "^[A-ZÀ-Ỹ][a-zà-ỹ]*(?:\\s+[A-ZÀ-Ỹ][a-zà-ỹ]*)*$", message = "INVALID_FULL_NAME")
    @NotBlank(message = "INVALID_FULL_NAME")
    @Length(max = 100, message = "INVALID_FULL_NAME")
    String fullName;

    @NotNull(message = "INVALID_DATE_OF_BIRTH")
    @ValidAge
    LocalDate dob;

    @NotBlank(message = "INVALID_ADDRESS")
    @Length(max = 50)
    String address;

    @NotBlank(message = "INVALID_DETAIL_ADDRESS")
    @Length(max = 100)
    String detailAddress;


}

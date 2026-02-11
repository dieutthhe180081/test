package com.sep490.g28.hvh.be.dto.host;

import com.sep490.g28.hvh.be.validation.RequiredField;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateMultipleHostAccountRequest {
    @Valid
    @RequiredField(fieldName = "Yêu cầu tạo tài khoản")
    List<CreateHostAccountRequest> requests;
}

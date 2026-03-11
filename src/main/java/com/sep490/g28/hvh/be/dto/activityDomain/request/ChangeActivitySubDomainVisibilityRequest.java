package com.sep490.g28.hvh.be.dto.activityDomain.request;

import com.sep490.g28.hvh.be.validation.RequiredField;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangeActivitySubDomainVisibilityRequest {
    @RequiredField(fieldName = "Hành động hiển thị")
    Boolean isVisible;
}

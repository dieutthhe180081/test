package com.sep490.g28.hvh.be.dto.activityDomain;

import com.sep490.g28.hvh.be.validation.RequiredField;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangeActivityDomainVisibilityRequest {
    @RequiredField(fieldName = "Hành động hiển thị")
    Boolean isVisible;
}

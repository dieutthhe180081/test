package com.sep490.g28.hvh.be.dto.activityDomain;

import com.sep490.g28.hvh.be.validation.RequiredField;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateActivitySubDomainRequest {

    Short id;

    @Length(max = 50)
    String name;

    @RequiredField(fieldName = "Thao tác")
    String action;
}

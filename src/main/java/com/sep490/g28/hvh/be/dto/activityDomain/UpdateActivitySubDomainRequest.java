package com.sep490.g28.hvh.be.dto.activityDomain;

import com.sep490.g28.hvh.be.validation.RequiredField;
import com.sep490.g28.hvh.be.validation.SubdomainUpdate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SubdomainUpdate
public class UpdateActivitySubDomainRequest {

    Short id;

    @Length(max = 50)
    String name;

    @RequiredField(fieldName = "Thao tác")
    String action;
}

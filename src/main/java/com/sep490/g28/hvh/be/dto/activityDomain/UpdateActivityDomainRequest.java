package com.sep490.g28.hvh.be.dto.activityDomain;

import com.sep490.g28.hvh.be.validation.RequiredField;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateActivityDomainRequest {

    @RequiredField(fieldName = "Tên lĩnh vực tình nguyện")
    @Length(max = 50)
    String name;

    @RequiredField(fieldName = "Thời gian giới hạn với lý do")
    @Min(value = 4)
    @Max(value = 12)
    Short specialSessionMaxTime;

    List<UpdateActivitySubDomainRequest> activitySubDomainUpdateRequests;
}

package com.sep490.g28.hvh.be.dto.event.request;

import com.sep490.g28.hvh.be.constant.EServedTarget;
import com.sep490.g28.hvh.be.constant.EServingPlaceType;
import com.sep490.g28.hvh.be.dto.checkinplace.request.UpdateCheckInPlaceRequest;
import com.sep490.g28.hvh.be.dto.eventImage.request.UpdateEventImageRequest;
import com.sep490.g28.hvh.be.validation.RequiredField;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateEventRequest {

    @NotBlank //todo null đuược nhưng khoong dduowcj khong hop le
    UUID eventId;

    @RequiredField(fieldName = "Tên sự kiện")
    String name;

    //todo
    List<UpdateEventImageRequest> updateImages;

    @RequiredField(fieldName = "Mô tả sự kiện")
    String description;

    @NotBlank
            //todo validate address
    String address;

    @NotNull
    Boolean autoApprove;

    @RequiredField(fieldName = "Lĩnh vực hoạt động")
    Short activitySubDomainId;

    @NotNull
            //todo
    int expectedVolAmount;

    @NotNull
            //todo
    int expectedSerAmount;

    @RequiredField(fieldName = "Đối tượng phục vụ")
    EServedTarget servedTarget;

    @RequiredField(fieldName = "Địa điểm phục vụ")
    EServingPlaceType servingPlaceType;

    //--------------------------------------------------------
//    todo validate date time
//    EarliestStartTime với LastestEndTime với DeèaultSessionMaxTime
//    Là 5, 23, và 4
    @NotNull
    LocalDate startDate;

    @NotNull
    LocalDate endDate;

    @NotNull
    LocalDate recruitmentEndDate;

    @NotNull
    OffsetDateTime startTime; // check-in time

    @NotNull
    OffsetDateTime endTime;   // check-out time

    //--------------------------------------------------------
    @RequiredField(fieldName = "Địa điểm phục vụ")
    @NotEmpty //todo have limit max 10
    @Valid
    List<UpdateCheckInPlaceRequest> checkInPlaces;

    @NotNull
    Boolean submit;
}

package com.sep490.g28.hvh.be.dto.event.request;

import com.sep490.g28.hvh.be.constant.EServedTarget;
import com.sep490.g28.hvh.be.constant.EServingPlaceType;
import com.sep490.g28.hvh.be.dto.eventimage.request.EditEventImageRequest;
import com.sep490.g28.hvh.be.validation.EventDate;
import com.sep490.g28.hvh.be.validation.RequiredField;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
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
@EventDate
public class EditEventRequest {

    //allowed null
    UUID eventId;

    @RequiredField(fieldName = "Tên sự kiện")
    String name;

    //todo validate bên trong update images
    @Valid
    List<EditEventImageRequest> updateImages;

    @RequiredField(fieldName = "Mô tả sự kiện")
    String description;

    @NotBlank
    //todo validate address
    String address;

    @NotNull(message = "INVALID_EVENT_AUTO_APPROVE")
    Boolean autoApprove;

    @NotNull(message = "INVALID_EVENT_SUBDOMAIN_ID")
    @Positive
    Short activitySubDomainId;

    @NotNull(message = "INVALID_EVENT_EXPECTED_VOL_AMOUNT")
    @PositiveOrZero( message = "INVALID_EVENT_EXPECTED_VOL_AMOUNT")
    @Max(value = 10000000, message = "INVALID_EVENT_EXPECTED_VOL_AMOUNT")
    Integer  expectedVolAmount;

    @NotNull(message = "INVALID_EVENT_EXPECTED_SER_AMOUNT")
    @PositiveOrZero( message = "INVALID_EVENT_EXPECTED_SER_AMOUNT")
    @Max(value = 10000000, message = "INVALID_EVENT_EXPECTED_SER_AMOUNT")
    Integer  expectedSerAmount;

    @RequiredField(fieldName = "Đối tượng phục vụ")
    EServedTarget servedTarget;

    @RequiredField(fieldName = "Địa điểm phục vụ")
    EServingPlaceType servingPlaceType;

    //--------------------------------------------------------
//    todo validate date
//    EarliestStartTime với LastestEndTime với DeèaultSessionMaxTime
//    Là 5, 23, và 4
    @RequiredField(fieldName = "Ngày bắt đầu sự kiện")
    LocalDate startDate;

    @RequiredField(fieldName = "Ngày kết thúc sự kiện")
    LocalDate endDate;

    @RequiredField(fieldName = "Ngày kết thúc tuyển người")
    LocalDate recruitmentEndDate;

    //todo, validate time
    @RequiredField(fieldName = "Thời gian bắt đầu sự kiện")
    OffsetDateTime startTime; // check-in time

    @RequiredField(fieldName = "Thời gian kết thúc sự kiện")
    OffsetDateTime endTime;   // check-out time

    //--------------------------------------------------------
    //todo, validate this
    @NotNull
    @Min(value = 10)
    Double checkInPlaceLat;

    @NotNull
    Double checkInPlaceLng;

    @NotNull
    Double checkInPlaceAccuracyMeters;
}

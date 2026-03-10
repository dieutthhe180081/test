package com.sep490.g28.hvh.be.dto.eventsession.request;

import com.sep490.g28.hvh.be.constant.EUpdateAction;
import com.sep490.g28.hvh.be.validation.EventTime;
import com.sep490.g28.hvh.be.validation.RequiredField;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@EventTime
public class EditEventSessionRequest {

    UUID eventDateTimeId; //nullable

    @RequiredField(fieldName = "updateAction")
    EUpdateAction updateAction;

    @RequiredField(fieldName = "Thời gian bắt đầu")
    OffsetDateTime startDateTime; // check-in time

    @RequiredField(fieldName = "Thời gian kết thúc")
    OffsetDateTime endDateTime;   // check-out time

    @NotNull(message = "INVALID_EVENT_EXPECTED_VOL_AMOUNT")
    @PositiveOrZero( message = "INVALID_EVENT_EXPECTED_VOL_AMOUNT")
    @Max(value = 10000000, message = "INVALID_EVENT_EXPECTED_VOL_AMOUNT")
    Integer  expectedVolAmount;

    @NotNull(message = "INVALID_EVENT_EXPECTED_SER_AMOUNT")
    @PositiveOrZero( message = "INVALID_EVENT_EXPECTED_SER_AMOUNT")
    @Max(value = 10000000, message = "INVALID_EVENT_EXPECTED_SER_AMOUNT")
    Integer  expectedSerAmount;
}

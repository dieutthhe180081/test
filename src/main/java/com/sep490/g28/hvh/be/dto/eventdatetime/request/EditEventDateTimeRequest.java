package com.sep490.g28.hvh.be.dto.eventdatetime.request;

import com.sep490.g28.hvh.be.constant.EUpdateAction;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EditEventDateTimeRequest {

    //todo validate this class
    UUID eventDateTimeId;

    @NotNull
    EUpdateAction updateAction;

    @NotNull
    OffsetDateTime startDateTime; // check-in time

    @NotNull
    OffsetDateTime endDateTime;   // check-out time

}

package com.sep490.g28.hvh.be.dto.checkinplace.request;

import com.sep490.g28.hvh.be.constant.EUpdateAction;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EditCheckInPlaceRequest {
    //todo validate this class
    UUID checkInPlaceId;

    EUpdateAction updateAction;

    @NotNull
    Double lat;

    @NotNull
    Double lng;

    @NotNull
    int accuracyMeters;
}

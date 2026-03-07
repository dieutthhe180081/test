package com.sep490.g28.hvh.be.dto.eventImage.request;

import com.sep490.g28.hvh.be.constant.EUpdateAction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventImageRequest {

    UUID imageId;

    @NotNull
    EUpdateAction updateAction;
//todo
//    @NotBlank
//    String uri;

    String fileExtension;
}

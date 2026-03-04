package com.sep490.g28.hvh.be.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ExceptionResponse {
    int code; //our code, not http
    String message;
    Map<String, String> moreInfo;
}

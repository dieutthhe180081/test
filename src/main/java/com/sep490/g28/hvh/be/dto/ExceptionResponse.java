package com.sep490.g28.hvh.be.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ExceptionResponse<T> {
    int code; //our code, not http
    String message;
    T moreInfo;
}

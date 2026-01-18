package com.sep490.g28.hvh.be.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatusCode;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ExceptionResponse<T> {
    int code;
    String message;
    T moreInfo;
}

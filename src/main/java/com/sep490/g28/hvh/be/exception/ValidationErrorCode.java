package com.sep490.g28.hvh.be.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
@AllArgsConstructor
public enum ValidationErrorCode {

    //from 1000
    INVALID_ERROR_CODE(1000, "Might have some spelling mistake in validation", HttpStatus.I_AM_A_TEAPOT),
    INVALID_DATA_TYPE(1001, "Invalid type for parameter: ", HttpStatus.BAD_REQUEST),
    INVALID_REQUEST_FORMAT(1002, "Invalid request format. Please check your input, there might be one field with wrong format.", HttpStatus.BAD_REQUEST),
    MISSING_QUERY_PARAM(1003, "Missing required parameter: ", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(1004, "Địa chỉ email không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1005, "Mật khẩu phải có ít nhất 8 kí tự, trong đó có ít nhất 1 chữ cái, 1 chữ số, 1 kí tự đặc biệt (!@#$%^&*.,:;’)", HttpStatus.BAD_REQUEST),
    INVALID_PHONE(1006, "Số điện thoại phải là số di động hợp lệ ở Việt Nam", HttpStatus.BAD_REQUEST),
    INVALID_CID(1007, "Số căn cước công dân không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_FILE_SIZE_MAX(1008, "{fieldName} phải có kích thước nhỏ hơn {maxFileSizeMb}Mb.", HttpStatus.BAD_REQUEST),
    INVALID_FILE_TYPE(1009, "{fieldName} phải là định dạng sau {allowedTypesMessage}.", HttpStatus.BAD_REQUEST),

    ;

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    public String formatMessage(Map<String, Object> params) {
        String result = message;
        for (var e : params.entrySet()) {
            result = result.replace(
                    "{" + e.getKey() + "}",
                    String.valueOf(e.getValue())
            );
        }
        return result;
    }
}

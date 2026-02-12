package com.sep490.g28.hvh.be.exception.errorCodeImpl;

import com.sep490.g28.hvh.be.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

/**
 * Contain Error code which relate to data validation
 * Code in format 2xxx
 */
@Getter
@AllArgsConstructor
public enum ValidationErrorCode implements ErrorCode {

    //from 1000
    INVALID_ERROR_CODE(2000, "Might have some spelling mistake in validation", HttpStatus.I_AM_A_TEAPOT),
    INVALID_DATA_TYPE(2001, "Invalid type for parameter: ", HttpStatus.BAD_REQUEST),
    INVALID_REQUEST_FORMAT(2002, "Invalid request format. Please check your input, there might be one field with wrong format.", HttpStatus.BAD_REQUEST),
    MISSING_QUERY_PARAM(2003, "Missing required parameter. ", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(2004, "Địa chỉ email không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(2005, "Mật khẩu phải có ít nhất 8 kí tự, trong đó có ít nhất 1 chữ cái, 1 chữ số, 1 kí tự đặc biệt (!@#$%^&*.,:;’)", HttpStatus.BAD_REQUEST),
    INVALID_PHONE(2006, "Số điện thoại phải là số di động hợp lệ ở Việt Nam", HttpStatus.BAD_REQUEST),
    INVALID_CID(2007, "Số căn cước công dân không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_FILE_SIZE_MAX(2008, "{fieldName} phải có kích thước nhỏ hơn {maxFileSizeMb}Mb.", HttpStatus.BAD_REQUEST),
    INVALID_FILE_TYPE(2009, "{fieldName} phải là định dạng .jpg/.jpeg,.png hoặc .pdf.", HttpStatus.BAD_REQUEST),
    INVALID_IMAGE_TYPE(2010, "{fieldName} phải là định dạng .jpg/.jpeg hoặc .png.", HttpStatus.BAD_REQUEST),
    INVALID_OTP(2011, "Mã OTP là chuỗi 6 kí tự chữ số", HttpStatus.BAD_REQUEST),
    INVALID_ORG_TYPE(2012, "Loại tổ chức không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_VOLUNTEER_VERIFICATION_STATUS(2013, "Trạng thái truyền vào không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_PAGE_NUMBER(2014, "Số trang phải là số nguyên >=0", HttpStatus.BAD_REQUEST),
    INVALID_PAGE_SIZE(2015, "Số lượng bản ghi trong một trang là số nguyên và giới hạn từ 1 tới 100", HttpStatus.BAD_REQUEST),
    INVALID_UUID(2016, "Định dạng ID không đúng", HttpStatus.BAD_REQUEST),
    MISSING_REQUIRED_FIELD(2017, "{fieldName} không được để trống", HttpStatus.BAD_REQUEST),
    INVALID_REJECTION_REASON(2018, "Lí do từ chối không được để trống", HttpStatus.BAD_REQUEST),
    INVALID_ORGANIZATION_REGISTRATION_STATUS(2019, "Trạng thái đơn đăng ký tổ chức truyền vào không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_FULL_NAME(2020, "Họ tên đầy đủ không hợp lệ (Viết hoa chữ cái đầu, không được chứa số hay kí tự đặc biệt, chỉ được 1 dấu cách giữa các từ).", HttpStatus.BAD_REQUEST),
    INVALID_DATE_OF_BIRTH(2021, "Tuổi của bạn phải từ {min} tới {max} tuổi.", HttpStatus.BAD_REQUEST),
    INVALID_ADDRESS(2022, "Địa chỉ không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_DETAIL_ADDRESS(2023, "Địa chỉ chi tiết không hợp lệ", HttpStatus.BAD_REQUEST),

    ;

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    public String formatMessage(Map<String, Object> params) {
        String result = this.getMessage();
        for (var e : params.entrySet()) {
            result = result.replace(
                    "{" + e.getKey() + "}",
                    String.valueOf(e.getValue())
            );
        }
        return result;
    }
}

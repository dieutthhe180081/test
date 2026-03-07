package com.sep490.g28.hvh.be.exception.errorCodeImpl;

import com.sep490.g28.hvh.be.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Contain Error code related to Event business rules
 * Code in format 7xxx
 */
@Getter
@AllArgsConstructor
public enum EventErrorCode implements ErrorCode {

    EVENT_NOT_FOUND(7001, "Event not found", HttpStatus.NOT_FOUND),

    EVENT_NOT_EDITABLE(7002, "Sự kiện đang ở trong trạng thái không thể chỉnh sửa được", HttpStatus.NOT_FOUND),

    INVALID_CHECKIN_PLACES_AMOUNT(7003, "Phải có ít nhất 1 và không vượt quá 10 địa điểm check in.", HttpStatus.BAD_REQUEST),
    INVALID_IMAGES_AMOUNT(7004, "Số lượng ảnh giới hạn tối đa 5 ảnh", HttpStatus.BAD_REQUEST),
    // todo missing validate error
    ;

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    @Override
    public String getName() {
        return this.name();
    }
}

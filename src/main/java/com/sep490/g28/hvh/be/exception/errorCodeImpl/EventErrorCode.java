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

    EVENT_NOT_EXISTED(7001, "Event not found", HttpStatus.NOT_FOUND),
    EVENT_NOT_EDITABLE(7002, "Sự kiện đang ở trong trạng thái không thể chỉnh sửa được", HttpStatus.NOT_FOUND),
    INVALID_IMAGES_AMOUNT(7003, "Số lượng ảnh giới hạn tối đa 5 ảnh", HttpStatus.BAD_REQUEST),
    INVALID_DATE_TIME_AMOUNT(7004, "Phải có ít nhất 1 ngày và thời gian diễn ra sự kiện", HttpStatus.BAD_REQUEST),
    INVALID_EVENT_DATE_TIME_RANGE(7005, "Thời gian bắt đầu sự kiện phải trước thời gian kết thúc trong ngày", HttpStatus.BAD_REQUEST),
    DUPLICATE_SESSION_DAY(7006, "Trong 1 ngày chỉ có 1 thời gian bắt đầu và thời gian kết thúc", HttpStatus.BAD_REQUEST),
    INVALID_EVENT_RECRUITMENT_END_DATE(7007, "Ngày kết thúc tuyển người phải cách ngày hôm nay ít nhất 3 ngày và trước ngày bắt đầu sự kiện ít nhất 3 ngày.", HttpStatus.BAD_REQUEST),
    INVALID_EVENT_START_DATE(7008, "Ngày bắt đầu tổ chức sự kiện phải cách ngày hôm nay ít nhất 15 ngày.", HttpStatus.BAD_REQUEST),
    INVALID_EVENT_END_DATE(7009, "Ngày kết thúc sự kiện phải sau ngày bắt đầu sự kiện.", HttpStatus.BAD_REQUEST),
    INVALID_EVENT_SESSION_TIME_RANGE(7010, "Khoảng cách giữa thời gian bắt đầu và kết thúc sự kiện trong 1 ngày phải nằm trong khoảng cho phép của lĩnh vực hoạt động.", HttpStatus.BAD_REQUEST),
    ;

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    @Override
    public String getName() {
        return this.name();
    }
}

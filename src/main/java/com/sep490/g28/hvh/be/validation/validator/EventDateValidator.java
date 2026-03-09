package com.sep490.g28.hvh.be.validation.validator;

import com.sep490.g28.hvh.be.dto.event.request.EditEventRequest;
import com.sep490.g28.hvh.be.exception.errorCodeImpl.ValidationErrorCode;
import com.sep490.g28.hvh.be.validation.EventDate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class EventDateValidator implements ConstraintValidator<EventDate, Object> {

    //today --------------------------------- >=15 days ------------------------ startDate ---- endDate
    //today -----  >=3 days --------- recruitmentEndDate ------ >=3 days ------- startDate

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext context) {
        LocalDate startDate;
        LocalDate endDate;
        LocalDate recruitmentEndDate;

        LocalDate today = LocalDate.now();

        if (o instanceof EditEventRequest req){
            startDate = req.getStartDate();
            endDate = req.getEndDate();
            recruitmentEndDate = req.getRecruitmentEndDate();
            //not validate null
            if (startDate == null || endDate == null || recruitmentEndDate == null) {
                return true;
            }

            boolean valid = true;

            //start date must after today 15 days
            if (startDate.isBefore(today.plusDays(15))) {
                invalid(context, "startDate",
                        ValidationErrorCode.INVALID_EVENT_START_DATE);
                valid = false;
            }

            //recruitmentEndDate must after today 3 days
            if (recruitmentEndDate.isBefore(today.plusDays(3))) {
                invalid(context, "recruitmentEndDate",
                        ValidationErrorCode.INVALID_EVENT_RECRUIT_END_DATE);
                valid = false;
            }

            //recruitmentEndDate must after before startDate 3 days
            if (recruitmentEndDate.isAfter(startDate.minusDays(3))) {
                invalid(context, "recruitmentEndDate",
                        ValidationErrorCode.INVALID_EVENT_RECRUIT_END_DATE);
                valid = false;
            }

            //endDate must after before startDate
            if (!endDate.isAfter(startDate)) {
                invalid(context, "endDate",
                        ValidationErrorCode.INVALID_EVENT_END_DATE);
                valid = false;
            }

            return valid;
        }



        return true;
    }

    //kinda throw exception :vv
    private void invalid(
            ConstraintValidatorContext context,
            String field,
            ValidationErrorCode errorCode
    ) {
        context.disableDefaultConstraintViolation();

        context.buildConstraintViolationWithTemplate(errorCode.name())
                .addPropertyNode(field)
                .addConstraintViolation();
    }
}

package com.sep490.g28.hvh.be.validation.volunteer;

import com.sep490.g28.hvh.be.dto.volunteer.request.VolunteerRegistrationVerifyRequest;
import com.sep490.g28.hvh.be.exception.errorCodeImpl.ValidationErrorCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class VolunteerRegistrationVerifyRequestTest {
    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private VolunteerRegistrationVerifyRequest validRequest() {
        VolunteerRegistrationVerifyRequest req =
                new VolunteerRegistrationVerifyRequest();
        req.setApprove(true);
        req.setRejectionReason(null);
        req.setFullName("Nguyễn Văn A");
        return req;
    }

    @Test
    void should_pass_when_all_fields_valid1() {
        Set<ConstraintViolation<VolunteerRegistrationVerifyRequest>> violations =
                validator.validate(validRequest());

        assertThat(violations).isEmpty();
    }

    @Test
    void should_pass_when_all_fields_valid2() {
        VolunteerRegistrationVerifyRequest req = validRequest();
        req.setApprove(false);
        req.setRejectionReason(null);
        req.setFullName(null);

        Set<ConstraintViolation<VolunteerRegistrationVerifyRequest>> violations =
                validator.validate(validRequest());

        assertThat(violations).isEmpty();
    }


    @Test
    void should_fail_when_approve_null() {
        VolunteerRegistrationVerifyRequest req = validRequest();
        req.setApprove(null);

        Set<ConstraintViolation<VolunteerRegistrationVerifyRequest>> violations =
                validator.validate(req);

        assertThat(violations.iterator().next().getMessage())
                .isEqualTo(ValidationErrorCode.MISSING_REQUIRED_FIELD.name());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "   "
    })
    void should_fail_when_rejection_reason_blank(String reason) {
        VolunteerRegistrationVerifyRequest req = validRequest();
        req.setRejectionReason(reason);

        Set<ConstraintViolation<VolunteerRegistrationVerifyRequest>> violations =
                validator.validate(req);

        assertThat(violations.iterator().next().getMessage())
                .isEqualTo(ValidationErrorCode.INVALID_REJECTION_REASON.name());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "   ",
            "nguyen van a",      // all are lower case
            "Nguyen van A",      // not Capitalize first char in a word
            "Nguyen  Van A",     // double space
            "Nguyen Van A1",     // has number
            "Nguyen@Van A",      // special char
            "Nguyen Van A  "       // special char
    })
    void should_fail_when_full_name_invalid(String fullName) {
        VolunteerRegistrationVerifyRequest req = validRequest();
        req.setFullName(fullName);

        Set<ConstraintViolation<VolunteerRegistrationVerifyRequest>> violations =
                validator.validate(req);

        assertThat(violations.iterator().next().getMessage())
                .isEqualTo(ValidationErrorCode.INVALID_FULL_NAME.name());
    }

    @Test
    void should_fail_when_full_name_exceed_100_chars() {
        VolunteerRegistrationVerifyRequest req = validRequest();
        req.setApprove(false);
        req.setFullName("A".repeat(101));

        Set<ConstraintViolation<VolunteerRegistrationVerifyRequest>> violations =
                validator.validate(req);

        assertThat(violations.iterator().next().getMessage())
                .isEqualTo(ValidationErrorCode.INVALID_FULL_NAME.name());
    }

}

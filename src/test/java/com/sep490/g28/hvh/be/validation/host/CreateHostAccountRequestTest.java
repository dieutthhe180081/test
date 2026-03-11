package com.sep490.g28.hvh.be.validation.host;

import com.sep490.g28.hvh.be.dto.host.request.CreateHostAccountRequest;
import com.sep490.g28.hvh.be.exception.errorCodeImpl.ValidationErrorCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class CreateHostAccountRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private CreateHostAccountRequest validRequest() {
        CreateHostAccountRequest r = new CreateHostAccountRequest();
        r.setCid("034309880903");
        r.setEmail("nguyenvanAn@gmail.com");
        r.setPhone("0916234940");
        r.setFullName("Nguyễn Văn An");
        r.setDob(LocalDate.now().minusYears(20));
        r.setAddress("Hà Nội");
        r.setDetailAddress("Ba Đình");
        return r;
    }

    //01
    @Test
    void validRequest_shouldHaveNoViolation() {
        Set<ConstraintViolation<CreateHostAccountRequest>> violations =
                validator.validate(validRequest());

        assertTrue(violations.isEmpty());
    }

    @Test
    void dobNull_shouldFail() {
        CreateHostAccountRequest r = validRequest();
        r.setDob(null);

        Set<ConstraintViolation<CreateHostAccountRequest>> v = validator.validate(r);

        assertEquals(1, v.size());
        assertEquals(ValidationErrorCode.INVALID_DATE_OF_BIRTH.name(), v.iterator().next().getMessage());
    }

    @Test
    void dobAgeEqualsMin_shouldPass() {
        CreateHostAccountRequest r = validRequest();
        r.setDob(LocalDate.now().minusYears(18));

        assertTrue(validator.validate(r).isEmpty());
    }

    @Test
    void dobAgeLessThanMin_shouldFail() {
        CreateHostAccountRequest r = validRequest();
        r.setDob(LocalDate.now().minusYears(17));

        Set<ConstraintViolation<CreateHostAccountRequest>> v = validator.validate(r);

        assertFalse(v.isEmpty());
    }

    @Test
    void dobAgeEqualsMax_shouldPass() {
        CreateHostAccountRequest r = validRequest();
        r.setDob(LocalDate.now().minusYears(80));

        assertTrue(validator.validate(r).isEmpty());
    }

    @Test
    void dobAgeGreaterThanMax_shouldFail() {
        CreateHostAccountRequest r = validRequest();
        r.setDob(LocalDate.now().minusYears(81));

        assertFalse(validator.validate(r).isEmpty());
    }

    @Test
    void dobInFuture_shouldFail() {
        CreateHostAccountRequest r = validRequest();
        r.setDob(LocalDate.now().plusDays(1));

        assertFalse(validator.validate(r).isEmpty());
    }

    @Test
    void should_fail_when_multiple_fields_invalid() {
        CreateHostAccountRequest req = validRequest();
        req.setCid("123");
        req.setEmail("abc");
        req.setPhone("9123456789");
        req.setFullName(null);

        Set<ConstraintViolation<CreateHostAccountRequest>> violations =
                validator.validate(req);

        assertThat(violations).hasSize(4);

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        ValidationErrorCode.INVALID_CID.name(),
                        ValidationErrorCode.INVALID_EMAIL.name(),
                        ValidationErrorCode.INVALID_PHONE.name(),
                        ValidationErrorCode.INVALID_FULL_NAME.name()

                );
    }
}

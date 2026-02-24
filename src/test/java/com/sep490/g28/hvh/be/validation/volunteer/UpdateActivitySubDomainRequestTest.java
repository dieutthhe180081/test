package com.sep490.g28.hvh.be.validation.volunteer;

import com.sep490.g28.hvh.be.dto.activityDomain.CreateActivityDomainRequest;
import com.sep490.g28.hvh.be.dto.activityDomain.UpdateActivityDomainRequest;
import com.sep490.g28.hvh.be.dto.activityDomain.UpdateActivitySubDomainRequest;
import com.sep490.g28.hvh.be.exception.errorCodeImpl.ValidationErrorCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UpdateActivitySubDomainRequestTest {
    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private UpdateActivitySubDomainRequest validRequest() {
        UpdateActivitySubDomainRequest req = new UpdateActivitySubDomainRequest();
        req.setId(Short.valueOf("1"));
        req.setName("Subdomain 1");
        req.setAction("EDIT");
        return req;
    }

    @Test
    void validRequest_shouldHaveNoViolation() {
        Set<ConstraintViolation<UpdateActivitySubDomainRequest>> violations =
                validator.validate(validRequest());

        assertTrue(violations.isEmpty());
    }

    @Test
    void nameLengthEqualsMax_shouldPass() {
        UpdateActivitySubDomainRequest r = validRequest();
        r.setName("a".repeat(50));

        assertTrue(validator.validate(r).isEmpty());
    }

    @Test
    void nameLengthGreaterThanMax_shouldFail() {
        UpdateActivitySubDomainRequest r = validRequest();
        r.setName("a".repeat(51));

        Set<ConstraintViolation<UpdateActivitySubDomainRequest>> v = validator.validate(r);

        assertFalse(v.isEmpty());
    }

    @Test
    void nameNullWhenActionIsEdit_shouldFail() {
        UpdateActivitySubDomainRequest r = validRequest();
        r.setName(null);
        r.setAction("EDIT");

        Set<ConstraintViolation<UpdateActivitySubDomainRequest>> v = validator.validate(r);

        assertEquals(1, v.size());
        assertEquals(ValidationErrorCode.INVALID_SUBDOMAIN_UPDATE.name(), v.iterator().next().getMessage());
    }

    @Test
    void idNullWhenActionIsEdit_shouldFail() {
        UpdateActivitySubDomainRequest r = validRequest();
        r.setId(null);
        r.setAction("EDIT");

        Set<ConstraintViolation<UpdateActivitySubDomainRequest>> v = validator.validate(r);

        assertEquals(1, v.size());
        assertEquals(ValidationErrorCode.INVALID_SUBDOMAIN_UPDATE.name(), v.iterator().next().getMessage());
    }

    @Test
    void idNullWhenActionIsDelete_shouldFail() {
        UpdateActivitySubDomainRequest r = validRequest();
        r.setId(null);
        r.setAction("DELETE");

        Set<ConstraintViolation<UpdateActivitySubDomainRequest>> v = validator.validate(r);

        assertEquals(1, v.size());
        assertEquals(ValidationErrorCode.INVALID_SUBDOMAIN_UPDATE.name(), v.iterator().next().getMessage());
    }

    @Test
    void nameNullWhenActionIsAdd_shouldFail() {
        UpdateActivitySubDomainRequest r = validRequest();
        r.setName(null);
        r.setAction("ADD");

        Set<ConstraintViolation<UpdateActivitySubDomainRequest>> v = validator.validate(r);

        assertEquals(1, v.size());
        assertEquals(ValidationErrorCode.INVALID_SUBDOMAIN_UPDATE.name(), v.iterator().next().getMessage());
    }
}

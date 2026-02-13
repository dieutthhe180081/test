package com.sep490.g28.hvh.be.validation.validator;

import com.sep490.g28.hvh.be.dto.activityDomain.UpdateActivitySubDomainRequest;
import com.sep490.g28.hvh.be.validation.SubdomainUpdate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SubdomainUpdateValidator implements ConstraintValidator<SubdomainUpdate, UpdateActivitySubDomainRequest> {

    @Override
    public boolean isValid(UpdateActivitySubDomainRequest updateActivitySubDomainRequest, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}

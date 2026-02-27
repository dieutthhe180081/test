package com.sep490.g28.hvh.be.validation.validator;

import com.sep490.g28.hvh.be.dto.activityDomain.UpdateActivitySubDomainRequest;
import com.sep490.g28.hvh.be.validation.SubdomainUpdate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SubdomainUpdateValidator implements ConstraintValidator<SubdomainUpdate, UpdateActivitySubDomainRequest> {

    @Override
    public boolean isValid(UpdateActivitySubDomainRequest request, ConstraintValidatorContext constraintValidatorContext) {

        Short id = request.getId();
        String name = request.getName();
        String action = request.getAction();

        switch (action) {
            case "EDIT":
                if(id == null || name == null) {
                    return false;
                }
            case "DELETE":
                if(id == null) {
                    return false;
                }
            case "ADD":
                if(name == null) {
                    return false;
                }
        }

        return true;
    }
}

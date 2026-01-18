package com.sep490.g28.hvh.be.validation.validator;

import com.sep490.g28.hvh.be.validation.FileType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

public class FileTypeValidator implements ConstraintValidator<FileType, MultipartFile> {
    private String[] allowedTypes;

    @Override
    public void initialize(FileType constraintAnnotation) {
        this.allowedTypes = constraintAnnotation.allowedTypes();
    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {

        context.disableDefaultConstraintViolation(); // tắt message mặc định

        // Nếu null thì không check size nữa
        if (multipartFile == null || multipartFile.isEmpty()) {
            return true;
        }
        String contentType = multipartFile.getContentType();

        if(!Arrays.asList(allowedTypes).contains(contentType)) {
            context.buildConstraintViolationWithTemplate("INVALID_FILE_TYPE")
                    .addConstraintViolation();
            return false;
        }
        return true;

    }
}

package com.sep490.g28.hvh.be.validation.validator;

import com.sep490.g28.hvh.be.validation.FileSizeMax;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class FileSizeMaxValidator implements ConstraintValidator<FileSizeMax, MultipartFile> {

    private int maxFileSizeMb;

    @Override
    public void initialize(FileSizeMax constraintAnnotation) {
        maxFileSizeMb = constraintAnnotation.maxFileSizeMb();
    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation(); // tắt message mặc định

        if (multipartFile == null || multipartFile.isEmpty()) {
            return true;
        }
        long maxSize = (long) maxFileSizeMb * 1024 * 1024;
        if(multipartFile.getSize() > maxSize) {
            context.buildConstraintViolationWithTemplate("INVALID_FILE_SIZE_MAX")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}


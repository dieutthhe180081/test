package com.sep490.g28.hvh.be.validation.validator;

import com.sep490.g28.hvh.be.validation.ImageMimeType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;


public class ImageMimeTypeValidator implements ConstraintValidator<ImageMimeType, String> {

    @Override
    public boolean isValid(String mimeType, ConstraintValidatorContext context) {

        try {
            MediaType mediaType = MediaType.parseMediaType(mimeType);
            // only allow image/jpeg and image/png
            return "image".equals(mediaType.getType())
                    && ("jpeg".equals(mediaType.getSubtype())
                    || "png".equals(mediaType.getSubtype()));

        } catch (InvalidMediaTypeException ex) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("INVALID_IMAGE_TYPE")
                    .addConstraintViolation();
            return false;
        }
    }
}

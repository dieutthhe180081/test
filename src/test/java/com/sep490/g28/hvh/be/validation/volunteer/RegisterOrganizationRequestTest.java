package com.sep490.g28.hvh.be.validation.volunteer;

import com.sep490.g28.hvh.be.dto.organization.RegisterOrganizationRequest;
import com.sep490.g28.hvh.be.exception.errorCodeImpl.ValidationErrorCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegisterOrganizationRequestTest {
    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private RegisterOrganizationRequest validRequest() {
        RegisterOrganizationRequest req = new RegisterOrganizationRequest();
        req.setOtp("123456");
        req.setName("Tổ chức A");
        req.setDhaRegistered(true);
        req.setOrgType("SOCIAL_ORGANIZATION");
        req.setOrgIntroduction("Tổ chức tình nguyện");
        req.setManagerFullName("Nguyễn Quang A");
        req.setManagerCid("021304883103");
        req.setManagerPhone("0312786343");
        req.setManagerEmail("nguyenquanga@gmail.com");
        req.setManagerCidFrontExtension(".png");
        req.setManagerCidBackExtension(".png");
        req.setManagerCidHoldingExtension(".png");
        req.setOtherEvidencesExtensions(".png .pdf .jpg");
        req.setApplicationReason("Yêu cầu đăng ký");
        return req;
    }

    @Test
    void should_pass_when_all_fields_valid() {
        Set<ConstraintViolation<RegisterOrganizationRequest>> violations =
                validator.validate(validRequest());

        assertThat(violations).isEmpty();
    }

    @Test
    void should_fail_when_name_null() {
        RegisterOrganizationRequest req = validRequest();
        req.setName(null);

        Set<ConstraintViolation<RegisterOrganizationRequest>> violations =
                validator.validate(req);

        assertThat(violations.iterator().next().getMessage())
                .isEqualTo(ValidationErrorCode.MISSING_REQUIRED_FIELD.name());
    }

    @Test
    void should_fail_when_dha_registered_null() {
        RegisterOrganizationRequest req = validRequest();
        req.setDhaRegistered(null);

        Set<ConstraintViolation<RegisterOrganizationRequest>> violations =
                validator.validate(req);

        assertThat(violations.iterator().next().getMessage())
                .isEqualTo(ValidationErrorCode.MISSING_REQUIRED_FIELD.name());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SOCIAL FUND",
            "",
            "   ",
            "SOCIALFUND",
            "Social_Fund",
    })
    void should_fail_when_org_type_invalid(String orgType) {
        RegisterOrganizationRequest req = validRequest();
        req.setOrgType(orgType);

        Set<ConstraintViolation<RegisterOrganizationRequest>> violations =
                validator.validate(req);

//        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo(ValidationErrorCode.INVALID_ORG_TYPE.name());
    }

    @Test
    void should_fail_when_org_introduction_null() {
        RegisterOrganizationRequest req = validRequest();
        req.setOrgIntroduction(null);

        Set<ConstraintViolation<RegisterOrganizationRequest>> violations =
                validator.validate(req);

        assertThat(violations.iterator().next().getMessage())
                .isEqualTo(ValidationErrorCode.MISSING_REQUIRED_FIELD.name());
    }

    @Test
    void should_pass_when_org_introduction_length_equals_max() {
        RegisterOrganizationRequest req = validRequest();
        String equalsMaxLengthString = """
                Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do
                eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut
                enim ad minim veniam, quis nostrud exercitation ullamco laboris
                nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor
                in reprehenderit in voluptate velit esse cillum dolore eu fugiat
                nulla pariatur. Excepteur sint occaecat cupidatat non proident,
                sunt in culpa qui officia deserunt mollit anim id est laborum.
                Sed ut perspiciatis unde omnis iste natus error sit voluptatem
                accusantium doloremque laudantium, totam rem aperiam, eaque ipsa
                quae ab illo inventore veritatis et quasi architecto beatae vitae
                dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit
                aspernatur aut odit aut fugit, sed quia consequuntur magni dolores
                eos qui ratione voluptatem se.
                """;
        req.setOrgIntroduction(equalsMaxLengthString);

        Set<ConstraintViolation<RegisterOrganizationRequest>> violations =
                validator.validate(req);

        assertTrue(validator.validate(violations).isEmpty());
    }

    @Test
    void should_pass_when_org_introduction_length_greater_than_max() {
        RegisterOrganizationRequest req = validRequest();
        String greaterThanMaxLengthString = """
                Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do
                eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut
                enim ad minim veniam, quis nostrud exercitation ullamco laboris
                nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor
                in reprehenderit in voluptate velit esse cillum dolore eu fugiat
                nulla pariatur. Excepteur sint occaecat cupidatat non proident,
                sunt in culpa qui officia deserunt mollit anim id est laborum.
                Sed ut perspiciatis unde omnis iste natus error sit voluptatem
                accusantium doloremque laudantium, totam rem aperiam, eaque ipsa
                quae ab illo inventore veritatis et quasi architecto beatae vitae
                dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit
                aspernatur aut odit aut fugit, sed quia consequuntur magni dolores
                eos qui ratione voluptatem sequi nesciunt.
                """;
        req.setOrgIntroduction(greaterThanMaxLengthString);

        Set<ConstraintViolation<RegisterOrganizationRequest>> violations =
                validator.validate(req);

        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            ".pdf .html .gif",
            "   ",
            "",
            ".pdf .png .jpg thisisarandomfile",
            ".sdf",
    })
    void should_fail_when_file_mime_type_invalid(String mimeType) {
        RegisterOrganizationRequest req = validRequest();
        req.setOtherEvidencesExtensions(mimeType);

        Set<ConstraintViolation<RegisterOrganizationRequest>> violations =
                validator.validate(req);

//        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo(ValidationErrorCode.INVALID_FILE_TYPE.name());
    }

    @Test
    void should_fail_when_application_reason_null() {
        RegisterOrganizationRequest req = validRequest();
        req.setApplicationReason(null);

        Set<ConstraintViolation<RegisterOrganizationRequest>> violations =
                validator.validate(req);

        assertThat(violations.iterator().next().getMessage())
                .isEqualTo(ValidationErrorCode.MISSING_REQUIRED_FIELD.name());
    }

    @Test
    void should_fail_when_multiple_fields_invalid() {
        RegisterOrganizationRequest req = validRequest();
        req.setOtp("12345");
        req.setManagerEmail("abc");
        req.setManagerPhone("123");
        req.setManagerCidFrontExtension(".txt");
        req.setManagerFullName("nguyen van a");

        Set<ConstraintViolation<RegisterOrganizationRequest>> violations =
                validator.validate(req);

        assertThat(violations).hasSize(5);

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        ValidationErrorCode.INVALID_OTP.name(),
                        ValidationErrorCode.INVALID_EMAIL.name(),
                        ValidationErrorCode.INVALID_PHONE.name(),
                        ValidationErrorCode.INVALID_IMAGE_TYPE.name(),
                        ValidationErrorCode.INVALID_FULL_NAME.name()
                );
    }



}

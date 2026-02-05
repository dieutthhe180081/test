package com.sep490.g28.hvh.be.integration.storage;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Utility component for generating standardized storage paths.
 * <p>
 * Centralizes path conventions to avoid hard-coded strings.
 * </p>
 */
@Component
public class StoragePathGenerator {

    private final static String IDENTITY_VERIFICATION_FOLDER = "/identity-verification";
    private final static String ORG_REGISTRATION_FOLDER = "/org-registration";

    private final static String CID_FRONT_FILE_NAME = "/cid-front";
    private final static String CID_BACK_FILE_NAME = "/cid-back";
    private final static String CID_HOLDING_FILE_NAME = "/cid-holding";
    private final static String OTHER_EVIDENCES_FILE_NAME = "/others_";



    public String identityVerificationCidFront(UUID verificationId, String mimeType){
        return  IDENTITY_VERIFICATION_FOLDER + "/" + verificationId + CID_FRONT_FILE_NAME + extFromMime(mimeType);
    }

    public String identityVerificationCidBack(UUID verificationId, String mimeType){
        return  IDENTITY_VERIFICATION_FOLDER + "/" + verificationId + CID_BACK_FILE_NAME + extFromMime(mimeType);
    }

    public String identityVerificationCidHolding(UUID verificationId, String mimeType){
        return  IDENTITY_VERIFICATION_FOLDER + "/" + verificationId + CID_HOLDING_FILE_NAME + extFromMime(mimeType);
    }

    public String orgRegistrationCidFront(UUID registrationId, String mimeType){
        return  ORG_REGISTRATION_FOLDER+ "/" + registrationId + CID_FRONT_FILE_NAME + extFromMime(mimeType);
    }

    public String orgRegistrationCidBack(UUID registrationId, String mimeType) {
        return  ORG_REGISTRATION_FOLDER+ "/" + registrationId + CID_BACK_FILE_NAME + extFromMime(mimeType);
    }

    public String orgRegistrationCidHolding(UUID registrationId, String mimeType) {
        return  ORG_REGISTRATION_FOLDER+ "/" + registrationId + CID_HOLDING_FILE_NAME + extFromMime(mimeType);
    }

    public String orgRegistrationOtherEvidences(UUID registrationId, int order, String mimeType) {
        return  ORG_REGISTRATION_FOLDER+ "/" + registrationId + OTHER_EVIDENCES_FILE_NAME + order + extFromMime(mimeType);
    }

    /**
     * Resolve file extension from MIME type.
     *
     * @param mimeType MIME type (e.g. image/jpeg)
     * @return file extension without dot
     * @throws IllegalArgumentException if MIME type unsupported
     */
    private static String extFromMime(String mimeType) {
        MediaType mediaType = MediaType.parseMediaType(mimeType);

        return switch (mediaType.getSubtype()) {
            case "jpeg", "jpg" -> "jpg";
            case "png" -> "png";
            case "webp" -> "webp";
            //todo bat exception cho cho nay
            default -> throw new IllegalArgumentException("UNSUPPORTED_MIME");
        };
    }
}

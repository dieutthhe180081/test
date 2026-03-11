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
    private final static String LEGAL_DOCUMENTS_FILE_NAME = "/legal-docs_";
    private final static String OTHER_EVIDENCES_FILE_NAME = "/others_";



    public String identityVerificationCidFront(UUID verificationId, String fileExtension){
        return  IDENTITY_VERIFICATION_FOLDER + "/" + verificationId + CID_FRONT_FILE_NAME + fileExtension;
    }

    public String identityVerificationCidBack(UUID verificationId, String fileExtension){
        return  IDENTITY_VERIFICATION_FOLDER + "/" + verificationId + CID_BACK_FILE_NAME + fileExtension;
    }

    public String identityVerificationCidHolding(UUID verificationId, String fileExtension){
        return  IDENTITY_VERIFICATION_FOLDER + "/" + verificationId + CID_HOLDING_FILE_NAME + fileExtension;
    }

    public String orgRegistrationCidFront(UUID registrationId, String fileExtension){
        return  ORG_REGISTRATION_FOLDER+ "/" + registrationId + CID_FRONT_FILE_NAME + fileExtension;
    }

    public String orgRegistrationCidBack(UUID registrationId, String fileExtension) {
        return  ORG_REGISTRATION_FOLDER+ "/" + registrationId + CID_BACK_FILE_NAME + fileExtension;
    }

    public String orgRegistrationCidHolding(UUID registrationId, String fileExtension) {
        return  ORG_REGISTRATION_FOLDER+ "/" + registrationId + CID_HOLDING_FILE_NAME + fileExtension;
    }

    public String orgRegistrationLegalDocuments(UUID registrationId, int order, String fileExtension) {
        return  ORG_REGISTRATION_FOLDER+ "/" + registrationId + LEGAL_DOCUMENTS_FILE_NAME + order + fileExtension;
    }

    public String orgRegistrationOtherEvidences(UUID registrationId, int order, String fileExtension) {
        return  ORG_REGISTRATION_FOLDER+ "/" + registrationId + OTHER_EVIDENCES_FILE_NAME + order + fileExtension;
    }
}

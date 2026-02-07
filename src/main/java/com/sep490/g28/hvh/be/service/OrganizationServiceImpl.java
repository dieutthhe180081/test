package com.sep490.g28.hvh.be.service;

import com.sep490.g28.hvh.be.constant.EOrgRegistrationStatus;
import com.sep490.g28.hvh.be.constant.EOrgType;
import com.sep490.g28.hvh.be.dto.organization.RegisterOrganizationRequest;
import com.sep490.g28.hvh.be.dto.organization.RegisterOrganizationResponse;
import com.sep490.g28.hvh.be.entity.OrganizationRegistration;
import com.sep490.g28.hvh.be.integration.cache.OtpService;
import com.sep490.g28.hvh.be.integration.storage.StoragePathGenerator;
import com.sep490.g28.hvh.be.integration.storage.StorageService;
import com.sep490.g28.hvh.be.repository.OrganizationRegistrationRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrganizationServiceImpl implements OrganizationService {

    OrganizationRegistrationRepository organizationRegistrationRepository;
    StorageService storageService;
    StoragePathGenerator storagePathGenerator;
    OtpService otpService;

    @Override
    public RegisterOrganizationResponse registerOrganization(RegisterOrganizationRequest request) {

        //1. validate otp
        otpService.verifyOrgRegistrationOtp(request.getManagerEmail(), request.getOtp());

        //2. check the unique email in the all system's account

        OrganizationRegistration org_registration = new OrganizationRegistration();
        UUID id = UUID.randomUUID();
        org_registration.setId(id);
        org_registration.setStatus(EOrgRegistrationStatus.PENDING);

        //3. generate upload url for fe
        //get the path in storage
        String managerCidFrontPath = storagePathGenerator.orgRegistrationCidFront(id, request.getManagerCidFrontExtension());
        String managerCidBackPath = storagePathGenerator.orgRegistrationCidBack(id, request.getManagerCidBackExtension());
        String managerCidHoldingPath = storagePathGenerator.orgRegistrationCidHolding(id, request.getManagerCidHoldingExtension());

        String[] otherEvidences = request.getOtherEvidencesExtensions().split("\\s+");
        List<String> otherEvidencesPathsList = new ArrayList<>();
        int order = 1;
        for (String otherEvidence : otherEvidences) {
            String otherEvidencePath = storagePathGenerator.orgRegistrationOtherEvidences(id, order++, otherEvidence);
            otherEvidencesPathsList.add(otherEvidencePath);
            if(order == 6) {
                break;
            }
        }
        StringBuilder otherEvidencesPathsSB = new StringBuilder();
        for (String otherEvidencePath : otherEvidencesPathsList) {
            otherEvidencesPathsSB.append(otherEvidencePath).append(" ");
        }
        String otherEvidencesPaths = otherEvidencesPathsSB.toString().trim();

        //get upload url
        CompletableFuture<String> managerCidFrontFuture =
                storageService.getUploadUrlAsync(managerCidFrontPath);
        CompletableFuture<String> managerCidBackFuture =
                storageService.getUploadUrlAsync(managerCidBackPath);
        CompletableFuture<String> managerCidHoldingFuture =
                storageService.getUploadUrlAsync(managerCidHoldingPath);

        List<CompletableFuture<String>> otherEvidencesFutures = new ArrayList<>();
        for (String otherEvidencePath : otherEvidencesPathsList) {
            CompletableFuture<String> otherEvidenceFuture =
                    storageService.getUploadUrlAsync(otherEvidencePath);
            otherEvidencesFutures.add(otherEvidenceFuture);
        }

        try {
            CompletableFuture.allOf(managerCidFrontFuture, managerCidBackFuture, managerCidHoldingFuture).join();
            CompletableFuture.allOf(otherEvidencesFutures.toArray(new CompletableFuture[0])).join();
        } catch (CompletionException e) {
            throw (RuntimeException) e.getCause();
        }

        String managerCidFrontUploadUrl = managerCidFrontFuture.join();
        String managerCidBackUploadUrl = managerCidBackFuture.join();
        String managerCidHoldingUploadUrl = managerCidHoldingFuture.join();
        StringBuilder otherEvidencesUploadUrlSB = new StringBuilder();
        for (CompletableFuture<String> otherEvidenceFuture : otherEvidencesFutures) {
            otherEvidencesUploadUrlSB.append(otherEvidenceFuture.join()).append(" ");
        }
        String otherEvidencesUploadUrl = otherEvidencesUploadUrlSB.toString().trim();

        //4. create organization registration in db
        org_registration.setName(request.getName());
        org_registration.setDhaRegistered(request.getDhaRegistered());
        org_registration.setOrgType(EOrgType.valueOf(request.getOrgType()));
        org_registration.setOrgIntroduction(request.getOrgIntroduction());
        org_registration.setManagerFullName(request.getManagerFullName());
        org_registration.setManagerCid(request.getManagerCid());
        org_registration.setManagerPhone(request.getManagerPhone());
        org_registration.setManagerEmail(request.getManagerEmail());
        org_registration.setApplicationReason(request.getApplicationReason());

        org_registration.setManagerCidFront(managerCidFrontPath);
        org_registration.setManagerCidBack(managerCidBackPath);
        org_registration.setManagerCidHolding(managerCidHoldingPath);
        org_registration.setOtherEvidences(otherEvidencesPaths);

        organizationRegistrationRepository.save(org_registration);
        log.info("Create new organization registration: {}", org_registration.getId());

        return RegisterOrganizationResponse.builder()
                .managerCidFrontUploadUrl(managerCidFrontUploadUrl)
                .managerCidBackUploadUrl(managerCidBackUploadUrl)
                .managerCidHoldingUploadUr(managerCidHoldingUploadUrl)
                .otherEvidences(otherEvidencesUploadUrl)
                .build();
    }
}

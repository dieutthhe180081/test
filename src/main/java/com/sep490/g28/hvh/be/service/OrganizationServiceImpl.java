package com.sep490.g28.hvh.be.service;

import com.sep490.g28.hvh.be.auth.CurrentUserProvider;
import com.sep490.g28.hvh.be.constant.EOrgRegistrationStatus;
import com.sep490.g28.hvh.be.constant.EOrgType;
import com.sep490.g28.hvh.be.constant.ERole;
import com.sep490.g28.hvh.be.dto.organization.*;
import com.sep490.g28.hvh.be.entity.Organization;
import com.sep490.g28.hvh.be.entity.OrganizationManager;
import com.sep490.g28.hvh.be.entity.OrganizationRegistration;
import com.sep490.g28.hvh.be.entity.SystemAdmin;
import com.sep490.g28.hvh.be.exception.AppException;
import com.sep490.g28.hvh.be.exception.errorCodeImpl.OrganizationErrorCode;
import com.sep490.g28.hvh.be.integration.authServer.AuthClient;
import com.sep490.g28.hvh.be.integration.cache.OtpService;
import com.sep490.g28.hvh.be.integration.mail.EmailService;
import com.sep490.g28.hvh.be.integration.storage.StoragePathGenerator;
import com.sep490.g28.hvh.be.integration.storage.StorageService;
import com.sep490.g28.hvh.be.repository.OrganizationManagerRepository;
import com.sep490.g28.hvh.be.repository.OrganizationRegistrationRepository;
import com.sep490.g28.hvh.be.repository.OrganizationRepository;
import com.sep490.g28.hvh.be.repository.SystemAdminRepository;
import com.sep490.g28.hvh.be.util.RandomStringUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
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
    OrganizationRepository organizationRepository;
    OrganizationManagerRepository organizationManagerRepository;
    StorageService storageService;
    StoragePathGenerator storagePathGenerator;
    OtpService otpService;
    SystemAdminRepository systemAdminRepository;
    CurrentUserProvider currentUserProvider;
    AuthClient authClient;
    EmailService emailService;

    @Override
    public RegisterOrganizationResponse registerOrganization(RegisterOrganizationRequest request) {

        //1. validate otp
        otpService.verifyOrgRegistrationOtp(request.getManagerEmail(), request.getOtp());

        //2. check the unique email in the all system's account

        OrganizationRegistration orgRegistration = new OrganizationRegistration();
        UUID id = UUID.randomUUID();
        orgRegistration.setId(id);
        orgRegistration.setStatus(EOrgRegistrationStatus.PENDING);

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
            if (order == 6) {
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
        List<String> otherEvidencesUploadUrl = new ArrayList<>();
        for (CompletableFuture<String> otherEvidenceFuture : otherEvidencesFutures) {
            otherEvidencesUploadUrl.add(otherEvidenceFuture.join());
        }

        //4. create organization registration in db
        orgRegistration.setName(request.getName());
        orgRegistration.setDhaRegistered(request.getDhaRegistered());
        orgRegistration.setOrgType(EOrgType.valueOf(request.getOrgType()));
        orgRegistration.setOrgIntroduction(request.getOrgIntroduction());
        orgRegistration.setManagerFullName(request.getManagerFullName());
        orgRegistration.setManagerCid(request.getManagerCid());
        orgRegistration.setManagerPhone(request.getManagerPhone());
        orgRegistration.setManagerEmail(request.getManagerEmail());
        orgRegistration.setApplicationReason(request.getApplicationReason());

        orgRegistration.setManagerCidFront(managerCidFrontPath);
        orgRegistration.setManagerCidBack(managerCidBackPath);
        orgRegistration.setManagerCidHolding(managerCidHoldingPath);
        orgRegistration.setOtherEvidences(otherEvidencesPaths);

        organizationRegistrationRepository.save(orgRegistration);
        log.info("Create new organization registration: {}", orgRegistration.getId());

        return RegisterOrganizationResponse.builder()
                .managerCidFrontUploadUrl(managerCidFrontUploadUrl)
                .managerCidBackUploadUrl(managerCidBackUploadUrl)
                .managerCidHoldingUploadUrl(managerCidHoldingUploadUrl)
                .otherEvidencesUploadUrls(otherEvidencesUploadUrl)
                .build();
    }

    @Override
    public Page<OrganizationRegistrationSimpleResponse> getOrgRegistrations(int pageNumber, int pageSize, String inputStatus, String managerEmail) {
        //parse status
        EOrgRegistrationStatus status =
                (inputStatus == null || inputStatus.isBlank())
                        ? null
                        : EOrgRegistrationStatus.valueOf(inputStatus);

        Pageable pageable = PageRequest.of(
                pageNumber,
                pageSize,
                Sort.by(Sort.Direction.ASC, "createdAt")
        );

        //search
        return organizationRegistrationRepository.search(status, managerEmail, pageable)
                .map(OrganizationRegistrationSimpleResponse::from);
    }

    @Override
    public OrganizationRegistrationDetailsResponse getOrgRegistrationDetails(UUID id) {
        //check id exist
        OrganizationRegistration organizationRegistration = organizationRegistrationRepository.findById(id).orElseThrow(
                () -> new AppException(OrganizationErrorCode.REGISTRATION_NOT_EXISTED)
        );

        String note = null;

        //get signed URL of file
        CompletableFuture<String> managerCidFrontFuture =
                storageService.getSignedUrlAsync(organizationRegistration.getManagerCidFront());
        CompletableFuture<String> managerCidBackFuture =
                storageService.getSignedUrlAsync(organizationRegistration.getManagerCidBack());
        CompletableFuture<String> managerCidHoldingFuture =
                storageService.getSignedUrlAsync(organizationRegistration.getManagerCidHolding());

        String[] otherEvidences = organizationRegistration.getOtherEvidences().split("\\s+");
        List<String> otherEvidencesList = new ArrayList<>(Arrays.asList(otherEvidences));
        List<CompletableFuture<String>> otherEvidencesFutures = new ArrayList<>();
        for (String otherEvidence : otherEvidencesList) {
            CompletableFuture<String> otherEvidenceFuture =
                    storageService.getSignedUrlAsync(otherEvidence);
            otherEvidencesFutures.add(otherEvidenceFuture);
        }

        String managerCidFrontUrl = null;
        String managerCidBackUrl = null;
        String managerCidHoldingUrl = null;
        List<String> otherEvidencesUrls = new ArrayList<>();

        try {
            CompletableFuture.allOf(managerCidFrontFuture, managerCidBackFuture, managerCidHoldingFuture).join();
            managerCidFrontUrl = managerCidFrontFuture.join();
            managerCidBackUrl = managerCidBackFuture.join();
            managerCidHoldingUrl = managerCidHoldingFuture.join();

            CompletableFuture.allOf(otherEvidencesFutures.toArray(new CompletableFuture[0])).join();
            for (CompletableFuture<String> otherEvidenceFuture : otherEvidencesFutures) {
                otherEvidencesUrls.add(otherEvidenceFuture.join());
            }

        } catch (CompletionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof AppException ae) {
                note = ae.getMessage() + "\n";
            } else {
                throw cause instanceof RuntimeException re ? re : e;
            }
        }

        return OrganizationRegistrationDetailsResponse.builder()
                .id(organizationRegistration.getId())
                .name(organizationRegistration.getName())
                .dhaRegistered(organizationRegistration.getDhaRegistered())
                .orgType(organizationRegistration.getOrgType())
                .orgIntroduction(organizationRegistration.getOrgIntroduction())
                .managerFullName(organizationRegistration.getManagerFullName())
                .managerCid(organizationRegistration.getManagerCid())
                .managerPhone(organizationRegistration.getManagerPhone())
                .managerEmail(organizationRegistration.getManagerEmail())
                .managerCidFrontUrl(managerCidFrontUrl)
                .managerCidBackUrl(managerCidBackUrl)
                .managerCidHoldingUrl(managerCidHoldingUrl)
                .otherEvidencesUrls(otherEvidencesUrls)
                .applicationReason(organizationRegistration.getApplicationReason())
                .status(organizationRegistration.getStatus())
                .rejectionReason(organizationRegistration.getRejectionReason())
                .createdAt(organizationRegistration.getCreatedAt())
                .reviewedAt(organizationRegistration.getReviewedAt())
                .adminId(organizationRegistration.getReviewedBy().getId())
                .organizationId(organizationRegistration.getOrganization().getId())
                .orgManagerId(organizationRegistration.getOrgManager().getId())
                .note(note)
                .build();
    }

    @Override
    public void verifyOrgRegistration(UUID id, OrganizationRegistrationVerifyRequest request) {
        //get the registration from db
        OrganizationRegistration organizationRegistration = organizationRegistrationRepository.findById(id).orElseThrow(
                () -> new AppException(OrganizationErrorCode.REGISTRATION_NOT_EXISTED)
        );

        if(!organizationRegistration.getStatus().equals(EOrgRegistrationStatus.PENDING)) {
            throw new AppException(OrganizationErrorCode.REGISTRATION_VERIFIED);
        }

        SystemAdmin currentAdmin = systemAdminRepository.getReferenceById(currentUserProvider.getId());
        organizationRegistration.setReviewedBy(currentAdmin);

        //delete cid images
        CompletableFuture<Void> f1 =
                storageService.deleteFileAsync(organizationRegistration.getManagerCidFront());
        CompletableFuture<Void> f2 =
                storageService.deleteFileAsync(organizationRegistration.getManagerCidBack());
        CompletableFuture<Void> f3 =
                storageService.deleteFileAsync(organizationRegistration.getManagerCidHolding());

        try {
            CompletableFuture.allOf(f1, f2, f3).join();
        } catch (CompletionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof AppException ae && ae.getHttpStatus().value() == 400) {
                //todo: this case is the file not exist in sb (only for test) change later, need to have picture to approve
            } else {
                throw (RuntimeException) e.getCause(); // propagate, transaction fail
            }
        }

        organizationRegistration.setManagerCidFront("");
        organizationRegistration.setManagerCidBack("");
        organizationRegistration.setManagerCidHolding("");

        if(Boolean.TRUE.equals(request.getApprove())) {
            //APPROVE
            //check the unique of email

            //Create organization in the db
            Organization organization = new Organization();
            organization.setName(organizationRegistration.getName());
            organization.setDhaRegistered(organizationRegistration.getDhaRegistered());
            organization.setOrgType(organizationRegistration.getOrgType());
            organization.setOrgIntroduction(organizationRegistration.getOrgIntroduction());
            organization.setOtherEvidences(organizationRegistration.getOtherEvidences());
            organization.setCreateBy(currentAdmin);

            organizationRepository.save(organization);

            //Create account in auth server
            String defaultPassword = RandomStringUtil.random8AlphaNumeric();
            UUID orgManagerId = authClient.createAccount(
                    ERole.ORG_MANAGER,
                    organizationRegistration.getManagerEmail(),
                    defaultPassword,
                    organizationRegistration.getManagerPhone()
            );

            //create organization manager in the db
            OrganizationManager organizationManager = new OrganizationManager();
            organizationManager.setId(orgManagerId);
            organizationManager.setCid(organizationRegistration.getManagerCid());
            organizationManager.setPhone(organizationRegistration.getManagerPhone());
            organizationManager.setEmail(organizationRegistration.getManagerEmail());
            organizationManager.setFullName(organizationRegistration.getManagerFullName());
            organizationManager.setOrganization(organization);
            organizationManager.setCreatedBy(currentAdmin);

            organizationManagerRepository.save(organizationManager);

            //update the organization registration record
            organizationRegistration.setStatus(EOrgRegistrationStatus.APPROVED);
            organizationRegistration.setReviewedBy(currentAdmin);
            organizationRegistration.setOrganization(organization);
            organizationRegistration.setOrgManager(organizationManager);

            organizationRegistrationRepository.save(organizationRegistration);

            emailService.sendApproveRegisterOrganizationEmail(organizationRegistration.getName(), organizationRegistration.getManagerEmail(), defaultPassword);

            log.info("Verify organization registration id={}, create organization manager account id={}", id, orgManagerId);
            return;
        }

        //REJECT
        //update the organization registration record
        organizationRegistration.setStatus(EOrgRegistrationStatus.REJECTED);
        organizationRegistration.setRejectionReason(request.getRejectionReason());
        organizationRegistrationRepository.save(organizationRegistration);
        //send email
        emailService.sendRejectRegisterOrganizationEmail(organizationRegistration.getManagerEmail(), request.getRejectionReason());
        log.info("Verify organization registration id={}, rejected", id);
    }
}

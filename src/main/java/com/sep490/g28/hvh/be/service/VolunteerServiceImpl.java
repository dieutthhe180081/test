package com.sep490.g28.hvh.be.service;

import com.sep490.g28.hvh.be.constant.EVolunteerVerificationStatus;
import com.sep490.g28.hvh.be.dto.volunteer.RegisterVolunteerAccountRequest;
import com.sep490.g28.hvh.be.dto.volunteer.RegisterVolunteerAccountResponse;
import com.sep490.g28.hvh.be.dto.volunteer.VolunteerRegistrationDetailsResponse;
import com.sep490.g28.hvh.be.dto.volunteer.VolunteerRegistrationSimpleResponse;
import com.sep490.g28.hvh.be.entity.IdentityVerification;
import com.sep490.g28.hvh.be.exception.AppException;
import com.sep490.g28.hvh.be.exception.errorCodeImpl.VolunteerErrorCode;
import com.sep490.g28.hvh.be.integration.cache.OtpService;
import com.sep490.g28.hvh.be.integration.storage.StoragePathGenerator;
import com.sep490.g28.hvh.be.integration.storage.StorageService;
import com.sep490.g28.hvh.be.repository.UserRepository;
import com.sep490.g28.hvh.be.repository.VolunteerRepository;
import com.sep490.g28.hvh.be.repository.IdentityVerificationRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VolunteerServiceImpl implements VolunteerService {

    VolunteerRepository volunteerRepository;
    UserRepository userRepository;
    IdentityVerificationRepository identityVerificationRepository;
    StorageService storageService;
    StoragePathGenerator storagePathGenerator;
    OtpService otpService;

    @Override
    public RegisterVolunteerAccountResponse registerVolAccount (RegisterVolunteerAccountRequest request) {

        //1. validate otp
        otpService.verifyVolAccountRegistrationOtp(request.getEmail(), request.getOtp());

        //2. check the unique email, cid, phone in the volunteers account
        if (volunteerRepository.existsByCid(request.getCid())) {
            throw new AppException(VolunteerErrorCode.CID_USED);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(VolunteerErrorCode.EMAIL_USED);
        }
        if (volunteerRepository.existsByPhone(request.getPhone())) {
            throw new AppException(VolunteerErrorCode.PHONE_USED);
        }

        IdentityVerification verification = new IdentityVerification();
        UUID id = UUID.randomUUID();
        verification.setId(id);
        verification.setStatus(EVolunteerVerificationStatus.PENDING);

        //3. generate upload url for fe
        //get the path in storage
        String cidFrontPath = storagePathGenerator.identityVerificationCidFront(id, request.getCidFrontFileExtension());
        String cidBackPath = storagePathGenerator.identityVerificationCidBack(id, request.getCidBackFileExtension());
        String cidHoldingPath = storagePathGenerator.identityVerificationCidHolding(id, request.getCidHoldingFileExtension());
        //get upload url
        CompletableFuture<String> cidFrontFuture =
                storageService.getUploadUrlAsync(cidFrontPath);
        CompletableFuture<String> cidBackFuture =
                storageService.getUploadUrlAsync(cidBackPath);
        CompletableFuture<String> cidHoldingFuture =
                storageService.getUploadUrlAsync(cidHoldingPath);

        try {
            CompletableFuture.allOf(cidFrontFuture, cidBackFuture, cidHoldingFuture).join();
        } catch (CompletionException e) {
            throw (RuntimeException) e.getCause();
        }

        String cidFrontUploadUrl = cidFrontFuture.join();
        String cidBackUploadUrl = cidBackFuture.join();
        String cidHoldingUploadUrl = cidHoldingFuture.join();

        //4. create volunteer verification request in db
        verification.setCid(request.getCid());
        verification.setEmail(request.getEmail());
        verification.setPhone(request.getPhone());

        verification.setCidFront(cidFrontPath);
        verification.setCidBack(cidBackPath);
        verification.setCidHolding(cidHoldingPath);

        identityVerificationRepository.save(verification);
        log.info("Create new identity verification: {}", verification.getId());

        return RegisterVolunteerAccountResponse.builder()
                .cidBackUploadUrl(cidBackUploadUrl)
                .cidFrontUploadUrl(cidFrontUploadUrl)
                .cidHoldingUploadUr(cidHoldingUploadUrl)
                .build();
    }

    @Override
    public Page<VolunteerRegistrationSimpleResponse> getRegistrations(int pageNumber, int pageSize, String inputStatus, String email) {
        //parse status
        EVolunteerVerificationStatus status =
                (inputStatus == null || inputStatus.isBlank())
                        ? null
                        : EVolunteerVerificationStatus.valueOf(inputStatus);

        Pageable pageable = PageRequest.of(
                pageNumber,
                pageSize,
                Sort.by(Sort.Direction.ASC, "createdAt")
        );

        //search
        return identityVerificationRepository.search(status, email, pageable)
                .map(VolunteerRegistrationSimpleResponse::from);
    }

    @Override
    public VolunteerRegistrationDetailsResponse getRegistrationDetails(UUID id) {
        //check id exist
        IdentityVerification identityVerification = identityVerificationRepository.findById(id).orElseThrow(
                () -> new AppException(VolunteerErrorCode.REGISTRATION_NOT_EXISTED)
        );

        String note = null;

        //get signed URL of file
        CompletableFuture<String> cidFrontFuture = storageService.getSignedUrlAsync(identityVerification.getCidFront());
        CompletableFuture<String>  cidBackFuture = storageService.getSignedUrlAsync(identityVerification.getCidBack());
        CompletableFuture<String>  cidHoldingFuture = storageService.getSignedUrlAsync(identityVerification.getCidHolding());

        String cidFrontUrl = null;
        String cidBackUrl = null;
        String cidHoldingUrl = null;

        try {
            CompletableFuture.allOf(cidFrontFuture, cidBackFuture, cidHoldingFuture).join();
            cidFrontUrl = cidFrontFuture.join();
            cidBackUrl = cidBackFuture.join();
            cidHoldingUrl = cidHoldingFuture.join();
        } catch (CompletionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof AppException ae && ae.getHttpStatus().value() == 400) {
                note = ae.getMessage();
            } else {
                throw cause instanceof RuntimeException re ? re : e;
            }
        }

        //check email exist in any account
        if (userRepository.existsByEmail(identityVerification.getEmail())) {
            //add to the note to announce sys_admin
            note = note + VolunteerErrorCode.EMAIL_USED.getMessage();
        }

        //build response
        return VolunteerRegistrationDetailsResponse.builder()
                .id(identityVerification.getId())
                .cid(identityVerification.getCid())
                .email(identityVerification.getEmail())
                .phone(identityVerification.getPhone())
                .cidFrontUrl(cidFrontUrl)
                .cidBackUrl(cidBackUrl)
                .cidHoldingUrl(cidHoldingUrl)
                .status(identityVerification.getStatus())
                .rejectionReason(identityVerification.getRejectionReason())
                .createdAt(identityVerification.getCreatedAt())
                .reviewAt(identityVerification.getReviewedAt())
                .note(note)
                .build();
    }

}

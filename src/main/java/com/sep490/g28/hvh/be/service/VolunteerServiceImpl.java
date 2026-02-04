package com.sep490.g28.hvh.be.service;

import com.sep490.g28.hvh.be.constant.EVolunteerVerificationStatus;
import com.sep490.g28.hvh.be.dto.volunteer.RegisterVolunteerAccountRequest;
import com.sep490.g28.hvh.be.dto.volunteer.RegisterVolunteerAccountResponse;
import com.sep490.g28.hvh.be.entity.IdentityVerification;
import com.sep490.g28.hvh.be.exception.AppException;
import com.sep490.g28.hvh.be.exception.errorCodeImpl.VolunteerErrorCode;
import com.sep490.g28.hvh.be.integration.cache.OtpService;
import com.sep490.g28.hvh.be.integration.storage.StoragePathGenerator;
import com.sep490.g28.hvh.be.integration.storage.StorageService;
import com.sep490.g28.hvh.be.repository.VolunteerRepository;
import com.sep490.g28.hvh.be.repository.IdentityVerificationRepository;
import io.netty.util.concurrent.CompleteFuture;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VolunteerServiceImpl implements VolunteerService {

    VolunteerRepository volunteerRepository;
    IdentityVerificationRepository identityVerificationRepository;
    StorageService storageService;
    StoragePathGenerator storagePathGenerator;
    OtpService otpService;

    @Override
    public RegisterVolunteerAccountResponse registerVolAccount (RegisterVolunteerAccountRequest request) {

        //1. validate otp
        otpService.verifyVerifyRegisterOtp(request.getEmail(), request.getOtp());

        //2. check the unique email, cid, phone in the volunteers account
        if (volunteerRepository.existsByCid(request.getCid())) {
            throw new AppException(VolunteerErrorCode.CID_USED);
        }
        if (volunteerRepository.existsByEmail(request.getEmail())) {
            throw new AppException(VolunteerErrorCode.EMAIL_USED);
        }
        if (volunteerRepository.existsByPhone(request.getPhone())) {
            throw new AppException(VolunteerErrorCode.PHONE_USED);
        }

        IdentityVerification verification = new IdentityVerification();
        UUID id = UUID.randomUUID();
        verification.setId(id);

        //3. generate upload url for fe
        //get the path in storage
        String cidFrontPath = storagePathGenerator.cidFront(id, request.getCidFrontMimeType());
        String cidBackPath = storagePathGenerator.cidBack(id, request.getCidBackMimeType());
        String cidHoldingPath = storagePathGenerator.cidHolding(id, request.getCidHoldingMimeType());
        //get upload url
        CompletableFuture<String> cidFrontFuture =
                storageService.getUploadUrlAsync(cidFrontPath);
        CompletableFuture<String> cidBackFuture =
                storageService.getUploadUrlAsync(cidBackPath);
        CompletableFuture<String> cidHoldingFuture =
                storageService.getUploadUrlAsync(cidHoldingPath);

        CompletableFuture.allOf(
                cidFrontFuture, cidBackFuture, cidHoldingFuture
        ).join();

        String cidFrontUploadUrl = cidFrontFuture.join();
        String cidBackUploadUrl = cidBackFuture.join();
        String cidHoldingUploadUrl = cidHoldingFuture.join();

        //4. create volunteer verification request in db
        verification.setStatus(EVolunteerVerificationStatus.PENDING);

        verification.setCid(request.getCid());
        verification.setEmail(request.getEmail());
        verification.setPhone(request.getPhone());

        verification.setCidFront(cidFrontPath);
        verification.setCidBack(cidBackPath);
        verification.setCidHolding(cidHoldingPath);

        identityVerificationRepository.save(verification);

        return RegisterVolunteerAccountResponse.builder()
                .cidBackUploadUrl(cidBackUploadUrl)
                .cidFrontUploadUrl(cidFrontUploadUrl)
                .cidHoldingUploadUr(cidHoldingUploadUrl)
                .build();
    }

}

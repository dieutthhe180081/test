package com.sep490.g28.hvh.be.service;

import com.sep490.g28.hvh.be.dto.volunteer.RegisterVolunteerAccountRequest;
import com.sep490.g28.hvh.be.dto.volunteer.RegisterVolunteerAccountResponse;
import com.sep490.g28.hvh.be.entity.IdentityVerification;
import com.sep490.g28.hvh.be.exception.errorCodeImpl.AppCommonErrorCode;
import com.sep490.g28.hvh.be.exception.AppException;
import com.sep490.g28.hvh.be.integration.cache.OtpService;
import com.sep490.g28.hvh.be.integration.storage.StoragePathGenerator;
import com.sep490.g28.hvh.be.integration.storage.StorageService;
import com.sep490.g28.hvh.be.repository.IdentityVerificationRepository;
import com.sep490.g28.hvh.be.repository.VolunteerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VolunteerServiceTest {

    @Mock
    VolunteerRepository volunteerRepository;
    @Mock
    IdentityVerificationRepository identityVerificationRepository;
    @Mock
    StorageService storageService;
    @Mock
    StoragePathGenerator storagePathGenerator;
    @Mock
    OtpService otpService;

    @InjectMocks
    VolunteerServiceImpl volunteerService;

    RegisterVolunteerAccountRequest request;

    @BeforeEach
    void setup() {
        request = new RegisterVolunteerAccountRequest();
        request.setOtp("123456");
        request.setEmail("nguyenvana@gmail.com");
        request.setPhone("0912345678");
        request.setCid("123456789012");
        request.setCidFrontMimeType("image/png");
        request.setCidBackMimeType("image/png");
        request.setCidHoldingMimeType("image/png");
    }

    @Test
    void register_success() {
        when(otpService.verifyVolAccountRegistrationOtp("nguyenvana@gmail.com", "123456"))
                .thenReturn(true);
        when(volunteerRepository.existsByCid(any())).thenReturn(false);
        when(volunteerRepository.existsByEmail(any())).thenReturn(false);
        when(volunteerRepository.existsByPhone(any())).thenReturn(false);

        when(storagePathGenerator.identityVerificationCidFront(any(), any())).thenReturn("front-path");
        when(storagePathGenerator.identityVerificationCidBack(any(), any())).thenReturn("back-path");
        when(storagePathGenerator.identityVerificationCidHolding(any(), any())).thenReturn("holding-path");

        when(storageService.getUploadUrlAsync("front-path"))
                .thenReturn(CompletableFuture.completedFuture("front-url"));
        when(storageService.getUploadUrlAsync("back-path"))
                .thenReturn(CompletableFuture.completedFuture("back-url"));
        when(storageService.getUploadUrlAsync("holding-path"))
                .thenReturn(CompletableFuture.completedFuture("holding-url"));

        RegisterVolunteerAccountResponse response =
                volunteerService.registerVolAccount(request);

        verify(otpService).verifyVolAccountRegistrationOtp("nguyenvana@gmail.com", "123456");
        verify(identityVerificationRepository).save(any(IdentityVerification.class));

        assertEquals("front-url", response.getCidFrontUploadUrl());
        assertEquals("back-url", response.getCidBackUploadUrl());
        assertEquals("holding-url", response.getCidHoldingUploadUr());
    }

    @Test
    void register_fail_invalid_otp() {
        doThrow(new AppException(AppCommonErrorCode.OTP_INVALID))
                .when(otpService)
                .verifyVolAccountRegistrationOtp(any(), any());

        assertThrows(AppException.class,
                () -> volunteerService.registerVolAccount(request));

        verify(identityVerificationRepository, never()).save(any());
    }

    @Test
    void register_fail_cid_used() {
        when(otpService.verifyVolAccountRegistrationOtp("nguyenvana@gmail.com", "123456"))
                .thenReturn(true);
        when(volunteerRepository.existsByCid(any())).thenReturn(true);

        assertThrows(AppException.class,
                () -> volunteerService.registerVolAccount(request));
    }

    @Test
    void register_fail_email_used() {
        when(otpService.verifyVolAccountRegistrationOtp("nguyenvana@gmail.com", "123456"))
                .thenReturn(true);
        when(volunteerRepository.existsByCid(any())).thenReturn(false);
        when(volunteerRepository.existsByEmail(any())).thenReturn(true);

        assertThrows(AppException.class,
                () -> volunteerService.registerVolAccount(request));
    }

    @Test
    void register_fail_phone_used() {
        when(otpService.verifyVolAccountRegistrationOtp("nguyenvana@gmail.com", "123456"))
                .thenReturn(true);
        when(volunteerRepository.existsByCid(any())).thenReturn(false);
        when(volunteerRepository.existsByEmail(any())).thenReturn(false);
        when(volunteerRepository.existsByPhone(any())).thenReturn(true);

        assertThrows(AppException.class,
                () -> volunteerService.registerVolAccount(request));
    }

}

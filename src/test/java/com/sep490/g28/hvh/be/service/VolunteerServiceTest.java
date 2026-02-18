package com.sep490.g28.hvh.be.service;

import com.sep490.g28.hvh.be.constant.EVolunteerVerificationStatus;
import com.sep490.g28.hvh.be.dto.volunteer.RegisterVolunteerAccountRequest;
import com.sep490.g28.hvh.be.dto.volunteer.RegisterVolunteerAccountResponse;
import com.sep490.g28.hvh.be.dto.volunteer.VolunteerRegistrationSimpleResponse;
import com.sep490.g28.hvh.be.entity.IdentityVerification;
import com.sep490.g28.hvh.be.exception.errorCodeImpl.AppCommonErrorCode;
import com.sep490.g28.hvh.be.exception.AppException;
import com.sep490.g28.hvh.be.exception.errorCodeImpl.VolunteerErrorCode;
import com.sep490.g28.hvh.be.integration.cache.OtpService;
import com.sep490.g28.hvh.be.integration.storage.StoragePathGenerator;
import com.sep490.g28.hvh.be.integration.storage.StorageService;
import com.sep490.g28.hvh.be.repository.IdentityVerificationRepository;
import com.sep490.g28.hvh.be.repository.UserRepository;
import com.sep490.g28.hvh.be.repository.VolunteerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VolunteerServiceTest {

    @Mock
    VolunteerRepository volunteerRepository;
    @Mock
    UserRepository userRepository;
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

    private RegisterVolunteerAccountRequest validRegisterVolunteerAccountRequest() {
        RegisterVolunteerAccountRequest req = new RegisterVolunteerAccountRequest();
        req.setOtp("123456");
        req.setEmail("nguyenvanA@gmail.com");
        req.setPhone("0916234940");
        req.setCid("034309880903");
        req.setCidFrontFileExtension(".jpeg");
        req.setCidBackFileExtension(".png");
        req.setCidHoldingFileExtension(".jpg");
        return req;
    }

    @Test
    void register_success() {
        RegisterVolunteerAccountRequest request = validRegisterVolunteerAccountRequest();
        when(otpService.verifyVolAccountRegistrationOtp(request.getEmail(), request.getOtp()))
                .thenReturn(true);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(volunteerRepository.existsByCid(any())).thenReturn(false);
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

        AppException ex = assertThrows(
                AppException.class,
                () -> volunteerService.registerVolAccount(validRegisterVolunteerAccountRequest())
        );
        assertEquals(AppCommonErrorCode.OTP_INVALID.getCode(), ex.getCode());

        verify(identityVerificationRepository, never()).save(any());
    }

    @Test
    void register_fail_cid_used() {
        RegisterVolunteerAccountRequest request = validRegisterVolunteerAccountRequest();
        when(otpService.verifyVolAccountRegistrationOtp(request.getEmail(), request.getOtp()))
                .thenReturn(true);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(volunteerRepository.existsByCid(any())).thenReturn(true);

        AppException ex = assertThrows(
                AppException.class,
                () -> volunteerService.registerVolAccount(request)
        );
        assertEquals(VolunteerErrorCode.CID_USED.getCode(), ex.getCode());
        verify(identityVerificationRepository, never()).save(any());

    }

    @Test
    void register_fail_email_used() {
        RegisterVolunteerAccountRequest request = validRegisterVolunteerAccountRequest();
        when(otpService.verifyVolAccountRegistrationOtp(request.getEmail(), request.getOtp()))
                .thenReturn(true);
        when(userRepository.existsByEmail(any())).thenReturn(true);

        AppException ex = assertThrows(
                AppException.class,
                () -> volunteerService.registerVolAccount(request)
        );
        assertEquals(VolunteerErrorCode.EMAIL_USED.getCode(), ex.getCode());
        verify(identityVerificationRepository, never()).save(any());

    }

    @Test
    void register_fail_phone_used() {
        RegisterVolunteerAccountRequest request = validRegisterVolunteerAccountRequest();
        when(otpService.verifyVolAccountRegistrationOtp(request.getEmail(), request.getOtp()))
                .thenReturn(true);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(volunteerRepository.existsByCid(any())).thenReturn(false);
        when(volunteerRepository.existsByPhone(any())).thenReturn(true);

        AppException ex = assertThrows(
                AppException.class,
                () -> volunteerService.registerVolAccount(request)
        );
        assertEquals(VolunteerErrorCode.PHONE_USED.getCode(), ex.getCode());
        verify(identityVerificationRepository, never()).save(any());
    }

    @Test
    void getRegistrations_should_return_page_with_status_and_email() {
        int pageNumber = 0;
        int pageSize = 10;
        String statusInput = "PENDING";
        String email = "nguyenvanA@gmail.com";

        IdentityVerification iv = new IdentityVerification();
        iv.setStatus(EVolunteerVerificationStatus.PENDING);

        Page<IdentityVerification> mockPage =
                new PageImpl<>(List.of(iv));

        when(identityVerificationRepository.search(
                eq(EVolunteerVerificationStatus.PENDING),
                eq(email),
                any(Pageable.class)
        )).thenReturn(mockPage);

        Page<VolunteerRegistrationSimpleResponse> result =
                volunteerService.getRegistrations(pageNumber, pageSize, statusInput, email);

        assertEquals(1, result.getTotalElements());

        verify(identityVerificationRepository)
                .search(eq(EVolunteerVerificationStatus.PENDING),
                        eq(email),
                        any(Pageable.class));
    }

    @Test
    void getRegistrations_should_pass_null_status_when_input_blank() {
        int pageNumber = 0;
        int pageSize = 10;

        Page<IdentityVerification> mockPage =
                new PageImpl<>(List.of(new IdentityVerification()));

        when(identityVerificationRepository.search(
                isNull(),
                isNull(),
                any(Pageable.class)
        )).thenReturn(mockPage);

        Page<VolunteerRegistrationSimpleResponse> result =
                volunteerService.getRegistrations(pageNumber, pageSize, "   ", null);

        assertEquals(1, result.getTotalElements());

        verify(identityVerificationRepository)
                .search(isNull(), isNull(), any(Pageable.class));
    }

}

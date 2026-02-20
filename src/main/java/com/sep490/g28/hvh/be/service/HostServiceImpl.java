package com.sep490.g28.hvh.be.service;

import com.sep490.g28.hvh.be.auth.CurrentUserProvider;
import com.sep490.g28.hvh.be.constant.ERole;
import com.sep490.g28.hvh.be.dto.host.CreateHostAccountRequest;
import com.sep490.g28.hvh.be.entity.Host;
import com.sep490.g28.hvh.be.entity.OrganizationManager;
import com.sep490.g28.hvh.be.exception.AppException;
import com.sep490.g28.hvh.be.exception.errorCodeImpl.HostErrorCode;
import com.sep490.g28.hvh.be.integration.authServer.AuthClient;
import com.sep490.g28.hvh.be.integration.mail.EmailService;
import com.sep490.g28.hvh.be.repository.HostRepository;
import com.sep490.g28.hvh.be.repository.OrganizationManagerRepository;
import com.sep490.g28.hvh.be.repository.UserRepository;
import com.sep490.g28.hvh.be.util.RandomStringUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.sep490.g28.hvh.be.util.StringNormalizeUtil.normalizeVietnameseName;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HostServiceImpl implements HostService{
    HostRepository hostRepository;
    OrganizationManagerRepository organizationManagerRepository;
    UserRepository userRepository;

    AuthClient authClient;
    EmailService emailService;

    CurrentUserProvider currentUserProvider;

//    @Transactional todo
    @Override
    public void createHostAccount(CreateHostAccountRequest request) {
        OrganizationManager organizationManager = organizationManagerRepository.getReferenceById(currentUserProvider.getId());

        //check whether email used by any account
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(HostErrorCode.EMAIL_USED);
        }
        //create account in supabase
        String defaultPassword = RandomStringUtil.random8AlphaNumeric();
        //create host account in auth server
        UUID hostId = authClient.createAccount(ERole.HOST, request.getEmail(), defaultPassword, request.getPhone());

        Host host = new Host();
        host.setId(hostId);
        host.setCid(request.getCid());
        host.setEmail(request.getEmail());
        host.setPhone(request.getPhone());
        host.setFullName(normalizeVietnameseName(request.getFullName()));
        host.setAddress(request.getAddress());
        host.setDetailAddress(request.getDetailAddress());
        host.setCreatedBy(organizationManager);
        host.setOrganization(organizationManager.getOrganization());

        //save to db
        hostRepository.save(host);
        log.info("Create host account, id={}", hostId);

        //send mail
        emailService.sendCreateHostAccountEmail(
                organizationManager.getOrganization().getName(),
                request.getEmail(),
                defaultPassword
        );
    }
}

package com.sep490.g28.hvh.be.service;

import com.sep490.g28.hvh.be.cache.RoleCache;
import com.sep490.g28.hvh.be.constant.ERole;
import com.sep490.g28.hvh.be.dto.supabase.CreateUserRequest;
import com.sep490.g28.hvh.be.dto.user.RegisterVolunteerAccountRequest;
import com.sep490.g28.hvh.be.dto.user.RegisterVolunteerAccountResponse;
import com.sep490.g28.hvh.be.entity.User;
import com.sep490.g28.hvh.be.integration.authServer.AuthService;
import com.sep490.g28.hvh.be.integration.storage.StoragePathGenerator;
import com.sep490.g28.hvh.be.integration.storage.StorageService;
import com.sep490.g28.hvh.be.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService{
    AuthService authService;
    StorageService storageService;
    UserRepository userRepository;
    RoleCache roleCache;
    StoragePathGenerator storagePathGenerator;

    @Override
    public RegisterVolunteerAccountResponse registerVolAccount (RegisterVolunteerAccountRequest requeste) {

        //todo kiem tra dong thong tin user nhap vao hop le chua, kieu chua ton tai chang han
        User user = new User();
        //create user in supabase
        //todo bắt exception có thể xảy ra
        UUID id = authService.createUser(new CreateUserRequest(requeste.getEmail(), requeste.getPassword(), normalizePhone(requeste.getPhone())));
        user.setId(id);
        user.setCid(requeste.getCid());

        //get the path in storage
        String cidFrontPath = storagePathGenerator.cidFront(id, requeste.getCidFrontMimeType());
        String cidBackPath = storagePathGenerator.cidBack(id, requeste.getCidBackMimeType());
        String cidHoldingPath = storagePathGenerator.cidHolding(id, requeste.getCidHoldingMimeType());

        user.setVerifyPhotos(cidFrontPath + ", " + cidBackPath + ", " + cidHoldingPath);

        //get upload url
        //todo nen chuyen cho nay thanh chay song song dong thoi
        String cidFrontUploadUrl = storageService.getUploadUrl(cidFrontPath, 600);
        String cidBackUploadUrl = storageService.getUploadUrl(cidBackPath, 600);
        String cidHoldingUploadUrl = storageService.getUploadUrl(cidHoldingPath, 600);


        //maybe tao request verify cho admin/ hoac la chi doi status thoi
        // todo đổi status cho user là được, admin sẽ lọc ra những cái pending

        user.setRole(roleCache.getByCode(ERole.VOL));
        userRepository.save(user);

        //todo giả sửa admin reject thì user làm như nào để verify lại thông tin

        return RegisterVolunteerAccountResponse.builder()
                .cidBackUploadUrl(cidBackUploadUrl)
                .cidFrontUploadUrl(cidFrontUploadUrl)
                .cidHoldingUploadUr(cidHoldingUploadUrl)
                .build();

    }

    /**
     * format phone number to E.164 standard
     * @param phone
     * @return
     */
    private String normalizePhone(String phone) {
        if (phone.startsWith("0")) {
            return "+84" + phone.substring(1);
        }
        return phone;
    }
}

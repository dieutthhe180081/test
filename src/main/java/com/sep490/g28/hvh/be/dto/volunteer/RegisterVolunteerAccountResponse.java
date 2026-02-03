package com.sep490.g28.hvh.be.dto.volunteer;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterVolunteerAccountResponse{

        private String cidFrontUploadUrl;
        private String cidBackUploadUrl;
        private String cidHoldingUploadUr;
}


package com.sep490.g28.hvh.be.notification.dto;

import lombok.Data;

@Data
public class SendPushRequest {
    private String token;
    private String title;
    private String body;
}

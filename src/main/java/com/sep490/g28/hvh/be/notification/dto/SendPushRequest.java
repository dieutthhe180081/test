package com.sep490.g28.hvh.be.notification.dto;

import lombok.Data;

import java.util.List;

@Data
public class SendPushRequest {
    //todo delete this class after finish, because this is used for testing notification
    private String token;
    private String title;
    private String body;
    private List<String> tokens;
}

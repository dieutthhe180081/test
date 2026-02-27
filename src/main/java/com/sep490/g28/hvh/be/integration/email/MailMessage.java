package com.sep490.g28.hvh.be.integration.email;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MailMessage {
    private String to;
    private String subject;
    private String body;
}
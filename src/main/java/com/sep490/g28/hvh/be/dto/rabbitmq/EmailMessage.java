package com.sep490.g28.hvh.be.dto.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailMessage {
    private String to;
    private String subject;
    private String body;
}
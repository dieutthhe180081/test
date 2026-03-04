package com.sep490.g28.hvh.be.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;

@Data
@AllArgsConstructor
public class TopicSubscriptionMessage {
    private String token;
    private Collection<String> topics;
}

package com.sep490.g28.hvh.be.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rabbitmq.mail")
public record RabbitMqMailProperties(
        String exchange,
        Queue queue,
        Routing routing,
        Retry retry
) {
    public record Queue(
            String send,
            String retry1,
            String retry2,
            String retry3,
            String dlq
    ) {}

    public record Routing(
            String send,
            String retry1,
            String retry2,
            String retry3,
            String dlq
    ) {}

    public record Retry(
            int ttl1,
            int ttl2,
            int ttl3
    ) {}
}
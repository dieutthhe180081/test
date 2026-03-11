package com.sep490.g28.hvh.be.integration.email;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Binds RabbitMQ email-related configuration properties.
 *
 * <p>Maps properties with prefix {@code rabbitmq.mail}
 * to strongly-typed objects used for email messaging setup.</p>
 *
 * <p>Includes exchange, queue names, routing keys,
 * and retry time-to-live (TTL) settings.</p>
 */
@ConfigurationProperties(prefix = "rabbitmq.email")
public record RabbitMqEmailProperties(
        String exchange,
        Queue queue,
        Routing routing,
        Retry retry
) {
    public record Queue(
            String send,
            String retry,
            String dlq
    ) {}

    public record Routing(
            String send,
            String retry,
            String dlq
    ) {}

    public record Retry(
            int ttl,
            int maxAttempts
    ) {}
}
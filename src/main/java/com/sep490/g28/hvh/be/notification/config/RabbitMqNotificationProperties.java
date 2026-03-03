package com.sep490.g28.hvh.be.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rabbitmq.notification")
public record RabbitMqNotificationProperties(

        String exchange,
        Queue queue,
        Routing routing,
        Retry retry

) {

    public record Queue(
            String sendUser,
            String sendTopic,
            String retryUser,
            String retryTopic,
            String dlqUser,
            String dlqTopic,

            String subscribe,
            String subscribeRetry,
            String subscribeDlq,

            String unsubscribe,
            String unsubscribeRetry,
            String unsubscribeDlq


            ) {}

    public record Routing(
            String sendUser,
            String sendTopic,
            String retryUser,
            String retryTopic,
            String dlqUser,
            String dlqTopic,

            String subscribe,
            String subscribeRetry,
            String subscribeDlq,

            String unsubscribe,
            String unsubscribeRetry,
            String unsubscribeDlq
    ) {}

    public record Retry(
            int ttl,
            int maxAttempts
    ) {}
}

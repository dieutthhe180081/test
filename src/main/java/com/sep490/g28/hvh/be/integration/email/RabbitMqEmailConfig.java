package com.sep490.g28.hvh.be.integration.email;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *Configuration for Message queue related to sending mail
 */
@Configuration
public class RabbitMqEmailConfig {

    private static final String HEADER_MESSAGE_TTL = "x-message-ttl";
    private static final String HEADER_DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";
    private static final String HEADER_DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";

    private final RabbitMqEmailProperties properties;

    public RabbitMqEmailConfig(RabbitMqEmailProperties properties) {
        this.properties = properties;
    }


    @Bean
    DirectExchange mailExchange() {
        return new DirectExchange(properties.exchange());
    }

    @Bean
    Queue sendQueue() {
        return QueueBuilder.durable(properties.queue().send())
                .withArgument(HEADER_DEAD_LETTER_EXCHANGE, properties.exchange()) //send mail fail, push to this exchange again with the routing below
                .withArgument(HEADER_DEAD_LETTER_ROUTING_KEY, properties.routing().retry())
                .build();
    }

    @Bean
    Queue retryQueue() {
        return QueueBuilder.durable(properties.queue().retry())
                .withArgument(HEADER_MESSAGE_TTL, properties.retry().ttl()) //milisecond
                .withArgument(HEADER_DEAD_LETTER_EXCHANGE, properties.exchange())
                .withArgument(HEADER_DEAD_LETTER_ROUTING_KEY, properties.routing().send()) //return to send queue after ttl time
                .build();
    }


    @Bean
    Queue mailDlqQueue() {
        return QueueBuilder.durable(properties.queue().dlq()).build();
    }

    @Bean
    Binding sendBinding() {
        return BindingBuilder.bind(sendQueue())
                .to(mailExchange())
                .with(properties.routing().send());
    }

    @Bean
    Binding retryBinding() {
        return BindingBuilder.bind(retryQueue())
                .to(mailExchange())
                .with(properties.routing().retry());
    }

    @Bean
    Binding dlqBinding() {
        return BindingBuilder.bind(mailDlqQueue())
                .to(mailExchange())
                .with(properties.routing().dlq());
    }
}

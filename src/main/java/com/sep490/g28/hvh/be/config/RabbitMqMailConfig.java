package com.sep490.g28.hvh.be.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMqMailConfig {

    private static final String HEADER_MESSAGE_TTL = "x-message-ttl";
    private static final String HEADER_DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";
    private static final String HEADER_DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";

    private final RabbitMqMailProperties properties;

    public RabbitMqMailConfig(RabbitMqMailProperties properties) {
        this.properties = properties;
    }


    @Bean
    TopicExchange mailExchange() {
        return new TopicExchange(properties.exchange());
    }

    @Bean
    Queue sendQueue() {
        return QueueBuilder.durable(properties.queue().send())
                .withArgument(HEADER_DEAD_LETTER_EXCHANGE, properties.exchange()) //send mail fail, push to this exchage a gain with the routing below
                .withArgument(HEADER_DEAD_LETTER_ROUTING_KEY, properties.routing().retry1())
                .build();
    }

    @Bean
    Queue retry1Queue() {
        return QueueBuilder.durable(properties.queue().retry1())
                .withArgument(HEADER_MESSAGE_TTL, properties.retry().ttl1()) //milisecond
                .withArgument(HEADER_DEAD_LETTER_EXCHANGE, properties.exchange())
                .withArgument(HEADER_DEAD_LETTER_ROUTING_KEY, properties.routing().retry2())
                .build();
    }

    @Bean
    Queue retry2Queue() {
        return QueueBuilder.durable(properties.queue().retry2())
                .withArgument(HEADER_MESSAGE_TTL, properties.retry().ttl2()) //milisecond
                .withArgument(HEADER_DEAD_LETTER_EXCHANGE, properties.exchange())
                .withArgument(HEADER_DEAD_LETTER_ROUTING_KEY, properties.routing().retry3())
                .build();
    }

    @Bean
    Queue retry3Queue() {
        return QueueBuilder.durable(properties.queue().retry3())
                .withArgument(HEADER_MESSAGE_TTL, properties.retry().ttl3()) //milisecond
                .withArgument(HEADER_DEAD_LETTER_EXCHANGE, properties.exchange())
                .withArgument(HEADER_DEAD_LETTER_ROUTING_KEY, properties.routing().dlq())
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
    Binding retry1Binding() {
        return BindingBuilder.bind(retry1Queue())
                .to(mailExchange())
                .with(properties.routing().retry1());
    }

    @Bean
    Binding retry2Binding() {
        return BindingBuilder.bind(retry2Queue())
                .to(mailExchange())
                .with(properties.routing().retry2());
    }

    @Bean
    Binding retry3Binding() {
        return BindingBuilder.bind(retry3Queue())
                .to(mailExchange())
                .with(properties.routing().retry3());
    }

    @Bean
    Binding dlqBinding() {
        return BindingBuilder.bind(mailDlqQueue())
                .to(mailExchange())
                .with(properties.routing().dlq());
    }

    @Bean
    Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

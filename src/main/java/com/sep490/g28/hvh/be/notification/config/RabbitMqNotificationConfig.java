package com.sep490.g28.hvh.be.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqNotificationConfig {

    private static final String HEADER_MESSAGE_TTL = "x-message-ttl";
    private static final String HEADER_DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";
    private static final String HEADER_DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";

    private final RabbitMqNotificationProperties properties;

    public RabbitMqNotificationConfig(RabbitMqNotificationProperties properties) {
        this.properties = properties;
    }

    @Bean
    DirectExchange notificationExchange() {
        return new DirectExchange(properties.exchange());
    }

//    ====================================
    @Bean
    Queue sendUserQueue() {
        return QueueBuilder.durable(properties.queue().sendUser())
                .withArgument(HEADER_DEAD_LETTER_EXCHANGE, properties.exchange()) //send mail fail, push to this exchage a gain with the routing below
                .withArgument(HEADER_DEAD_LETTER_ROUTING_KEY, properties.routing().retryUser())
                .build();
    }

    @Bean
    Queue sendTopicQueue() {
        return QueueBuilder.durable(properties.queue().sendTopic())
                .withArgument(HEADER_DEAD_LETTER_EXCHANGE, properties.exchange()) //send mail fail, push to this exchage a gain with the routing below
                .withArgument(HEADER_DEAD_LETTER_ROUTING_KEY, properties.routing().retryTopic())
                .build();
    }

    @Bean
    Queue retryUserQueue(){
        return QueueBuilder.durable(properties.queue().retryUser())
                .withArgument(HEADER_MESSAGE_TTL, properties.retry().ttl()) //milisecond
                .withArgument(HEADER_DEAD_LETTER_EXCHANGE, properties.exchange())
                .withArgument(HEADER_DEAD_LETTER_ROUTING_KEY, properties.routing().sendUser())
                .build();
    }

    @Bean
    Queue retryTopicQueue(){
        return QueueBuilder.durable(properties.queue().retryTopic())
                .withArgument(HEADER_MESSAGE_TTL, properties.retry().ttl()) //milisecond
                .withArgument(HEADER_DEAD_LETTER_EXCHANGE, properties.exchange())
                .withArgument(HEADER_DEAD_LETTER_ROUTING_KEY, properties.routing().sendTopic())
                .build();
    }

    @Bean
    Queue dlqUserQueue(){
        return QueueBuilder.durable(properties.queue().dlqUser()).build();
    }

    @Bean
    Queue dlqTopicQueue(){
        return QueueBuilder.durable(properties.queue().dlqTopic()).build();
    }


    @Bean
    Binding sendUserBinding() {
        return BindingBuilder
                .bind(sendUserQueue())
                .to(notificationExchange())
                .with(properties.routing().sendUser());
    }

    @Bean
    Binding sendTopicBinding() {
        return BindingBuilder
                .bind(sendTopicQueue())
                .to(notificationExchange())
                .with(properties.routing().sendTopic());
    }

    @Bean
    Binding retryUserBinding() {
        return BindingBuilder
                .bind(retryUserQueue())
                .to(notificationExchange())
                .with(properties.routing().retryUser());
    }

    @Bean
    Binding retryTopicBinding() {
        return BindingBuilder
                .bind(retryTopicQueue())
                .to(notificationExchange())
                .with(properties.routing().retryTopic());
    }

    @Bean
    Binding dlqUserBinding() {
        return BindingBuilder
                .bind(dlqUserQueue())
                .to(notificationExchange())
                .with(properties.routing().dlqUser());
    }

    @Bean
    Binding dlqTopicBinding() {
        return BindingBuilder
                .bind(dlqTopicQueue())
                .to(notificationExchange())
                .with(properties.routing().dlqTopic());
    }

    //====================================
    @Bean
    Queue subscribeQueue(){
        return QueueBuilder.durable(properties.queue().subscribe())
                .withArgument(HEADER_DEAD_LETTER_EXCHANGE, properties.exchange())
                .withArgument(HEADER_DEAD_LETTER_ROUTING_KEY, properties.routing().subscribeRetry())
                .build();
    }

    @Bean
    Queue subscribeRetryQueue(){
        return QueueBuilder.durable(properties.queue().subscribeRetry())
                .withArgument(HEADER_MESSAGE_TTL, properties.retry().ttl()) //milisecond
                .withArgument(HEADER_DEAD_LETTER_EXCHANGE, properties.exchange())
                .withArgument(HEADER_DEAD_LETTER_ROUTING_KEY, properties.routing().subscribe())
                .build();
    }

    @Bean
    Queue subscribeDlqQueue(){
        return QueueBuilder.durable(properties.queue().subscribeDlq())
                .build();
    }

    @Bean
    Binding subscribeBinding() {
        return BindingBuilder
                .bind(subscribeQueue())
                .to(notificationExchange())
                .with(properties.routing().subscribe());
    }

    @Bean
    Binding subscribeRetryBinding() {
        return BindingBuilder
                .bind(subscribeRetryQueue())
                .to(notificationExchange())
                .with(properties.routing().subscribeRetry());
    }

    @Bean
    Binding subscribeDlqBinding() {
        return BindingBuilder
                .bind(subscribeDlqQueue())
                .to(notificationExchange())
                .with(properties.routing().subscribeDlq());
    }


    //====================================
    @Bean
    Queue unsubscribeQueue(){
        return QueueBuilder.durable(properties.queue().unsubscribe())
                .withArgument(HEADER_DEAD_LETTER_EXCHANGE, properties.exchange())
                .withArgument(HEADER_DEAD_LETTER_ROUTING_KEY, properties.routing().unsubscribeRetry())
                .build();
    }

    @Bean
    Queue unsubscribeRetryQueue(){
        return QueueBuilder.durable(properties.queue().unsubscribeRetry())
                .withArgument(HEADER_MESSAGE_TTL, properties.retry().ttl()) //milisecond
                .withArgument(HEADER_DEAD_LETTER_EXCHANGE, properties.exchange())
                .withArgument(HEADER_DEAD_LETTER_ROUTING_KEY, properties.routing().unsubscribe())
                .build();
    }

    @Bean
    Queue unsubscribeDlqQueue(){
        return QueueBuilder.durable(properties.queue().unsubscribeDlq())
                .build();
    }

    @Bean
    Binding unsubscribeBinding() {
        return BindingBuilder
                .bind(unsubscribeQueue())
                .to(notificationExchange())
                .with(properties.routing().unsubscribe());
    }

    @Bean
    Binding unsubscribeRetryBinding() {
        return BindingBuilder
                .bind(unsubscribeRetryQueue())
                .to(notificationExchange())
                .with(properties.routing().unsubscribeRetry());
    }

    @Bean
    Binding unsubscribeDlqBinding() {
        return BindingBuilder
                .bind(unsubscribeDlqQueue())
                .to(notificationExchange())
                .with(properties.routing().unsubscribeDlq());
    }

}

package com.hyperativatechtest.features.common.config.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";
    public static final String DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";

    @Bean
    public DirectExchange cardExchange() {
        return new DirectExchange(Exchanges.CARD_EXCHANGE);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(Exchanges.DEAD_LETTER_EXCHANGE);
    }

    @Bean
    public Queue fileProcessingQueue() {
        return QueueBuilder.durable(Queues.FILE_PROCESSING_QUEUE)
                .withArgument(DEAD_LETTER_EXCHANGE, Exchanges.DEAD_LETTER_EXCHANGE)
                .withArgument(DEAD_LETTER_ROUTING_KEY, Queues.FILE_PROCESSING_DLQ)
                .build();
    }

    @Bean
    public Queue fileProcessingDlq() {
        return QueueBuilder.durable(Queues.FILE_PROCESSING_DLQ).build();
    }

    @Bean
    public Queue cardSaveQueue() {
        return QueueBuilder.durable(Queues.CARD_SAVE_QUEUE)
                .withArgument(DEAD_LETTER_EXCHANGE, Exchanges.DEAD_LETTER_EXCHANGE)
                .withArgument(DEAD_LETTER_ROUTING_KEY, Queues.CARD_SAVE_DLQ)
                .build();
    }

    @Bean
    public Queue cardSaveDlq() {
        return QueueBuilder.durable(Queues.CARD_SAVE_DLQ).build();
    }

    @Bean
    public Binding fileProcessingBinding(Queue fileProcessingQueue, DirectExchange cardExchange) {
        return BindingBuilder.bind(fileProcessingQueue)
                .to(cardExchange)
                .with(RoutingKeys.FILE_PROCESSING_ROUTING_KEY);
    }

    @Bean
    public Binding cardSaveBinding(Queue cardSaveQueue, DirectExchange cardExchange) {
        return BindingBuilder.bind(cardSaveQueue)
                .to(cardExchange)
                .with(RoutingKeys.CARD_SAVE_ROUTING_KEY);
    }

    @Bean
    public Binding fileProcessingDlqBinding(Queue fileProcessingDlq, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(fileProcessingDlq)
                .to(deadLetterExchange)
                .with(Queues.FILE_PROCESSING_DLQ);
    }

    @Bean
    public Binding cardSaveDlqBinding(Queue cardSaveDlq, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(cardSaveDlq)
                .to(deadLetterExchange)
                .with(Queues.CARD_SAVE_DLQ);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        return rabbitTemplate;
    }
}


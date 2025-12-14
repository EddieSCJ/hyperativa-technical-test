package com.hyperativatechtest.features.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMQPublisherService implements MessagePublisherService {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(String exchange, Map<String, Object> message, Optional<String> routingKey) {
        try {
            String key = routingKey.orElse("");
            rabbitTemplate.convertAndSend(exchange, key, message);
            log.debug("Published message to exchange: {}, routing key: {}", exchange, key);
        } catch (Exception e) {
            log.error("Error publishing message to exchange: {}, routing key: {}", exchange, routingKey, e);
            throw new RuntimeException("Error publishing to RabbitMQ: " + e.getMessage(), e);
        }
    }
}


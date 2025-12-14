package com.hyperativatechtest.features.common.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("RabbitMQPublisherService Tests")
class RabbitMQPublisherServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private RabbitMQPublisherService publisherService;

    private Map<String, Object> message;

    @BeforeEach
    void setUp() {
        message = new HashMap<>();
        message.put("jobId", "job123");
        message.put("data", "test data");
    }

    @Test
    @DisplayName("Should publish message with routing key")
    void shouldPublishMessageWithRoutingKey() {
        publisherService.publish("test.exchange", message, Optional.of("test.routing.key"));

        verify(rabbitTemplate).convertAndSend("test.exchange", "test.routing.key", message);
    }

    @Test
    @DisplayName("Should publish message without routing key")
    void shouldPublishMessageWithoutRoutingKey() {
        publisherService.publish("test.exchange", message, Optional.empty());

        verify(rabbitTemplate).convertAndSend("test.exchange", "", message);
    }


    @Test
    @DisplayName("Should handle RabbitTemplate exceptions")
    void shouldHandleRabbitTemplateExceptions() {
        doThrow(new RuntimeException("Connection failed"))
                .when(rabbitTemplate).convertAndSend(eq("test.exchange"), eq("test.key"), any(Map.class));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> publisherService.publish("test.exchange", message, Optional.of("test.key")));

        assertTrue(exception.getMessage().contains("Error publishing to RabbitMQ"));
        assertTrue(exception.getCause().getMessage().contains("Connection failed"));
    }

    @Test
    @DisplayName("Should handle null routing key")
    void shouldHandleNullRoutingKey() {
        publisherService.publish("test.exchange", message, Optional.ofNullable(null));

        verify(rabbitTemplate).convertAndSend("test.exchange", "", message);
    }
}

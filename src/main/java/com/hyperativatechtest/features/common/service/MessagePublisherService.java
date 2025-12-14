package com.hyperativatechtest.features.common.service;

import java.util.Map;
import java.util.Optional;

public interface MessagePublisherService {

    void publish(String channel, Map<String, Object> message, Optional<String> routingKey);
}


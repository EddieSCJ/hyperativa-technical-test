package com.hyperativatechtest.features.common.config.rabbitmq;

public class Queues {
    public static final String FILE_PROCESSING_QUEUE = "file.processing.queue";
    public static final String CARD_SAVE_QUEUE = "card.save.queue";
    public static final String FILE_PROCESSING_DLQ = "file.processing.dlq";
    public static final String CARD_SAVE_DLQ = "card.save.dlq";

    private Queues() {
    }
}


package com.hyperativatechtest.features.card.exception;

public class CardFileUploadException extends RuntimeException {
    public CardFileUploadException(String message) {
        super(message);
    }

    public CardFileUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}


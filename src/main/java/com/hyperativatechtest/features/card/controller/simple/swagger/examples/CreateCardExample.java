package com.hyperativatechtest.features.card.controller.simple.swagger.examples;

public class CreateCardExample {

    public static final String REQUEST = """
        {
          "cardNumber": "4456897922969999"
        }
        """;

    public static final String CARD_CREATED_201_OK = """
        {
          "id": "550e8400-e29b-41d4-a716-446655440000",
          "maskedCardNumber": "****97999999",
          "lotId": null,
          "sequenceNumber": null,
          "createdAt": "2024-12-14T02:50:00Z"
        }
        """;

    public static final String ERROR_INVALID_CARD_NUMBER = """
        {
          "status": 400,
          "message": "cardNumber: Card number must be between 13 and 19 digits"
        }
        """;

    public static final String ERROR_MALFORMED_JSON = """
        {
          "status": 400,
          "message": "Malformed JSON request"
        }
        """;

    public static final String ERROR_CARD_ALREADY_EXISTS = """
        {
          "status": 409,
          "message": "Card already exists in the system"
        }
        """;

    public static final String ERROR_UNAUTHORIZED = """
        {
          "status": 401,
          "message": "Unauthorized access - valid JWT token required"
        }
        """;

    public static final String ERROR_INTERNAL_SERVER = """
        {
          "status": 500,
          "message": "An unexpected error occurred"
        }
        """;

    private CreateCardExample() {
    }
}


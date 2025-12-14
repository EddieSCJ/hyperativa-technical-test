package com.hyperativatechtest.features.card.controller.simple.swagger.examples;

public class LookupCardExample {

    public static final String REQUEST = """
        {
          "cardNumber": "4456897922969999"
        }
        """;

    public static final String CARD_FOUND_200_OK = """
        {
          "found": true,
          "id": "550e8400-e29b-41d4-a716-446655440000",
          "maskedCardNumber": "****97999999"
        }
        """;

    public static final String ERROR_INVALID_CARD_NUMBER = """
        {
          "status": 400,
          "message": "cardNumber: Card number must be between 13 and 19 digits"
        }
        """;

    public static final String ERROR_UNAUTHORIZED = """
        {
          "status": 401,
          "message": "Unauthorized access - valid JWT token required"
        }
        """;

    private LookupCardExample() {
    }
}

package org.example.supermarketpos.dto;

// ErrorResponse.java
public class ErrorResponse {
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

    // Getter
}
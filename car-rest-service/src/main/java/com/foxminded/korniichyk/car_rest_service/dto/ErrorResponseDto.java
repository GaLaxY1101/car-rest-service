package com.foxminded.korniichyk.car_rest_service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponseDto {

    private String message;
    private int statusCode;
    private LocalDateTime timestamp;

    public ErrorResponseDto(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
        this.timestamp = LocalDateTime.now();
    }

}

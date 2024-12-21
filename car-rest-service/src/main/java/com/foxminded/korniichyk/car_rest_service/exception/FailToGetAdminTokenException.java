package com.foxminded.korniichyk.car_rest_service.exception;

public class FailToGetAdminTokenException extends RuntimeException {
    public FailToGetAdminTokenException(String message) {
        super(message);
    }
}

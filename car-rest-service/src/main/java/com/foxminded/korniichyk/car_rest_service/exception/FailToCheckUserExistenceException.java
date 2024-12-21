package com.foxminded.korniichyk.car_rest_service.exception;

public class FailToCheckUserExistenceException extends RuntimeException {
    public FailToCheckUserExistenceException(String message) {
        super(message);
    }
}

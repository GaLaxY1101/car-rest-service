package com.foxminded.korniichyk.car_rest_service.exception;

public class UserWithProvidedEmailAlreadyExistsException extends RuntimeException {
    public UserWithProvidedEmailAlreadyExistsException(String message) {
        super(message);
    }
}

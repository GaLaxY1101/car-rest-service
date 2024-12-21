package com.foxminded.korniichyk.car_rest_service.exception;

public class JsonCustomException extends RuntimeException {
    public JsonCustomException(String message) {
        super(message);
    }
}

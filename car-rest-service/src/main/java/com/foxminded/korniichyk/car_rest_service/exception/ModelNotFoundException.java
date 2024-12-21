package com.foxminded.korniichyk.car_rest_service.exception;

import jakarta.persistence.EntityNotFoundException;

public class ModelNotFoundException extends EntityNotFoundException {
    public ModelNotFoundException(String message) {
        super(message);
    }
}

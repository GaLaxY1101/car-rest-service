package com.foxminded.korniichyk.car_rest_service.exception;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;

public class EngineNotFoundException extends EntityNotFoundException {
    public EngineNotFoundException(String message) {
        super(message);
    }
}

package com.foxminded.korniichyk.car_rest_service.exception;

import jakarta.persistence.EntityNotFoundException;

public class CarNotFoundException extends EntityNotFoundException {
    public CarNotFoundException(String message) {
        super(message);
    }
}

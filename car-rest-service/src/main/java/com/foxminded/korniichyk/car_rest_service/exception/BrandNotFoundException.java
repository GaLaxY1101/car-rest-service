package com.foxminded.korniichyk.car_rest_service.exception;

import jakarta.persistence.EntityNotFoundException;

public class BrandNotFoundException extends EntityNotFoundException {
    public BrandNotFoundException(String message) {
        super(message);
    }
}

package com.foxminded.korniichyk.car_rest_service.dto.brand;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class BrandUpdateRequestDto {

    @NotEmpty(message = "Name shouldn't be empty")
    private String name;

}

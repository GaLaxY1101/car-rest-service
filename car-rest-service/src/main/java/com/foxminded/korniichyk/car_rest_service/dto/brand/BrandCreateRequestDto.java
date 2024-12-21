package com.foxminded.korniichyk.car_rest_service.dto.brand;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class BrandCreateRequestDto {

    @NotEmpty(message = "Name shouldn't be empty")
    private String name;

}

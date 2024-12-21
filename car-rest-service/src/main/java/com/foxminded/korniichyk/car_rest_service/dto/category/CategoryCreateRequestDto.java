package com.foxminded.korniichyk.car_rest_service.dto.category;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CategoryCreateRequestDto {

    @NotEmpty(message = "Category must have name")
    private String name;

}

package com.foxminded.korniichyk.car_rest_service.dto.engine;

import com.foxminded.korniichyk.car_rest_service.model.Engine;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EngineCreateRequestDto {

    @NotEmpty(message = "Name shouldn't be empty")
    private String name;

    @NotNull(message = "Capacity shouldn't be empty")
    private Double capacity;

    @NotNull(message = "Type shouldn't be empty")
    private Engine.Type type;

}

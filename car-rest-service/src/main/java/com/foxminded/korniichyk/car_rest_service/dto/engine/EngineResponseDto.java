package com.foxminded.korniichyk.car_rest_service.dto.engine;

import com.foxminded.korniichyk.car_rest_service.model.Car;
import com.foxminded.korniichyk.car_rest_service.model.Engine;
import lombok.Data;

@Data
public class EngineResponseDto {

    private Long id;

    private String name;

    private Double capacity;

    private Engine.Type type;

}

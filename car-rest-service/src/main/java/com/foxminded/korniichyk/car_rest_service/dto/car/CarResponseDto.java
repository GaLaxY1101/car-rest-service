package com.foxminded.korniichyk.car_rest_service.dto.car;

import com.foxminded.korniichyk.car_rest_service.model.Car;
import lombok.Data;

@Data
public class CarResponseDto {

    private Long id;

    private String modelName;

    private String engineName;

    private String engineCapacity;

    private String categoryName;

    private String serialNumber;

    private Car.Drive drive;

    private String color;

}

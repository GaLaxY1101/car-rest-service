package com.foxminded.korniichyk.car_rest_service.dto.model;

import lombok.Data;

@Data
public class ModelResponseDto {

    private Long id;
    private String name;
    private String startManufacturing;
    private String endManufacturing;
    private String brandName;
    private String generation;

}

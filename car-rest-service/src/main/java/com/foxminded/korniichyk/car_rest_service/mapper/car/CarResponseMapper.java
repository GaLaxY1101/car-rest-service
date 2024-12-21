package com.foxminded.korniichyk.car_rest_service.mapper.car;

import com.foxminded.korniichyk.car_rest_service.dto.car.CarResponseDto;
import com.foxminded.korniichyk.car_rest_service.model.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(
            componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface CarResponseMapper {

    @Mapping(target = "modelName", source = "model.name")
    @Mapping(target = "engineName", source = "engine.name")
    @Mapping(target = "engineCapacity", source = "engine.capacity")
    @Mapping(target = "categoryName", source = "category.name")
    CarResponseDto toDto(Car car);

}

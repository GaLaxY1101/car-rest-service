package com.foxminded.korniichyk.car_rest_service.mapper.car;

import com.foxminded.korniichyk.car_rest_service.dto.car.CarCreateRequestDto;
import com.foxminded.korniichyk.car_rest_service.model.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface CarCreateRequestMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "model", ignore = true)
    @Mapping(target = "engine", ignore = true)
    @Mapping(target = "manufacturingDate", source = "manufacturingDate", qualifiedByName = "stringToInstant")
    Car toEntity(CarCreateRequestDto carCreateRequestDto);

    @Named(value = "stringToInstant")
    default Instant stringToInstant(String date) {
        return Instant.parse(date);
    }

}

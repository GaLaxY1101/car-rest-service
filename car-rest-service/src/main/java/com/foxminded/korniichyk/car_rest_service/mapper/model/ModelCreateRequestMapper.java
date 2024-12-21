package com.foxminded.korniichyk.car_rest_service.mapper.model;

import com.foxminded.korniichyk.car_rest_service.dto.model.ModelCreateRequestDto;
import com.foxminded.korniichyk.car_rest_service.model.Model;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface ModelCreateRequestMapper {


    @Mapping(target = "startManufacturing", source = "startManufacturing", qualifiedByName = "stringToInstant")
    @Mapping(target = "endManufacturing", source = "endManufacturing", qualifiedByName = "stringToInstant")
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "id", ignore = true)
    Model toEntity(ModelCreateRequestDto modelCreateRequestDto);

    @Named(value = "stringToInstant")
    default Instant stringToInstant(String date) {
        return Instant.parse(date);
    }
}

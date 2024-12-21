package com.foxminded.korniichyk.car_rest_service.mapper.model;

import com.foxminded.korniichyk.car_rest_service.dto.model.ModelResponseDto;

import com.foxminded.korniichyk.car_rest_service.model.Model;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ModelResponseMapper {

    @Mapping(target = "brandName", source = "brand.name")
    ModelResponseDto toDto(Model model);

}

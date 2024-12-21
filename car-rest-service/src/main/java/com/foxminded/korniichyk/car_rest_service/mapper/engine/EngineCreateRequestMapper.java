package com.foxminded.korniichyk.car_rest_service.mapper.engine;

import com.foxminded.korniichyk.car_rest_service.dto.engine.EngineCreateRequestDto;
import com.foxminded.korniichyk.car_rest_service.model.Engine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EngineCreateRequestMapper {

    @Mapping(target = "id", ignore = true)
    Engine toEntity(EngineCreateRequestDto engineCreateRequestDto);

}

package com.foxminded.korniichyk.car_rest_service.mapper.engine;

import com.foxminded.korniichyk.car_rest_service.dto.engine.EngineResponseDto;
import com.foxminded.korniichyk.car_rest_service.model.Engine;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EngineResponseMapper {

    EngineResponseDto toDto(Engine engine);

}

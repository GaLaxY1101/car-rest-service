package com.foxminded.korniichyk.car_rest_service.mapper.brand;


import com.foxminded.korniichyk.car_rest_service.dto.brand.BrandResponseDto;
import com.foxminded.korniichyk.car_rest_service.model.Brand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BrandResponseMapper {

    BrandResponseDto toDto(Brand brand);

}

package com.foxminded.korniichyk.car_rest_service.mapper.category;

import com.foxminded.korniichyk.car_rest_service.dto.category.CategoryResponseDto;
import com.foxminded.korniichyk.car_rest_service.model.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryResponseMapper {

    CategoryResponseDto toDto(Category category);

}

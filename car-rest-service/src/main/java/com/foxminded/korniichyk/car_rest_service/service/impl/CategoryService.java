package com.foxminded.korniichyk.car_rest_service.service.impl;

import com.foxminded.korniichyk.car_rest_service.dto.category.CategoryCreateRequestDto;
import com.foxminded.korniichyk.car_rest_service.dto.category.CategoryResponseDto;
import com.foxminded.korniichyk.car_rest_service.dto.category.CategoryUpdateRequestDto;
import com.foxminded.korniichyk.car_rest_service.exception.CategoryNotFoundException;
import com.foxminded.korniichyk.car_rest_service.mapper.category.CategoryResponseMapper;
import com.foxminded.korniichyk.car_rest_service.model.Category;
import com.foxminded.korniichyk.car_rest_service.repository.CategoryRepository;
import com.foxminded.korniichyk.car_rest_service.service.contract.CrudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService implements CrudService<Category, Long> {


    private final CategoryRepository categoryRepository;
    private final CategoryResponseMapper categoryResponseMapper;

    @Transactional
    @Override
    public void save(Category entity) {
        categoryRepository.save(entity);
        log.info("{} saved", entity);
    }

    @Transactional
    @Override
    public void delete(Long id) {

        if (!categoryRepository.existsById(id)) {
            log.error("Category with id: '{}' not found", id);
            throw new CategoryNotFoundException("No such category");
        }

        categoryRepository.deleteById(id);
    }

    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Category with id: '{}' not found", id);
                    return new CategoryNotFoundException("No such category");
                });
    }

    @Override
    public Page<Category> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Transactional
    public CategoryResponseDto createCategory(CategoryCreateRequestDto categoryCreateRequestDto) {
        Category category = new Category();
        category.setName(categoryCreateRequestDto.getName());

        save(category);

        return categoryResponseMapper.toDto(category);
    }

    @Transactional
    public CategoryResponseDto update(Long id, CategoryUpdateRequestDto categoryUpdateRequestDto) {
        Category category = findById(id);
        category.setName(categoryUpdateRequestDto.getName());

        return categoryResponseMapper.toDto(category);
    }
}

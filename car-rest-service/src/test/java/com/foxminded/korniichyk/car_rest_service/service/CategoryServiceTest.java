package com.foxminded.korniichyk.car_rest_service.service;

import com.foxminded.korniichyk.car_rest_service.dto.category.CategoryCreateRequestDto;
import com.foxminded.korniichyk.car_rest_service.dto.category.CategoryResponseDto;
import com.foxminded.korniichyk.car_rest_service.dto.category.CategoryUpdateRequestDto;
import com.foxminded.korniichyk.car_rest_service.exception.CategoryNotFoundException;
import com.foxminded.korniichyk.car_rest_service.mapper.category.CategoryResponseMapper;
import com.foxminded.korniichyk.car_rest_service.mapper.category.CategoryResponseMapperImpl;
import com.foxminded.korniichyk.car_rest_service.model.Category;
import com.foxminded.korniichyk.car_rest_service.repository.CategoryRepository;
import com.foxminded.korniichyk.car_rest_service.service.impl.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.foxminded.korniichyk.car_rest_service.util.TestUtil.createCategory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Spy
    private CategoryResponseMapper categoryResponseMapper = new CategoryResponseMapperImpl();

    @InjectMocks
    private CategoryService categoryService;


    @Test
    public void delete_shouldThrowCategoryNotFoundException_whenCategoryDoesntExist() {
        when(categoryRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(CategoryNotFoundException.class, () -> categoryService.delete(1L));
        verify(categoryRepository, never()).delete(any(Category.class));

    }

    @Test
    public void delete_shouldDeleteCategory_whenExists() {
        Category category = createCategory();
        when(categoryRepository.existsById(anyLong())).thenReturn(true);
        categoryService.delete(category.getId());

        verify(categoryRepository).deleteById(category.getId());
    }

    @Test
    public void findById_shouldThrowCategoryNotFoundException_whenCategoryDoesntExist() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.findById(1L));
    }

    @Test
    public void findById_shouldReturnCategory_whenCategoryExists() {
        Category category = createCategory();
        category.setName("SUV");

        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));

        Category foundCategory = categoryService.findById(category.getId());

        assertNotNull(foundCategory);
        assertEquals("SUV", foundCategory.getName());
    }

    @Test
    public void createCategory_shouldCreateCategoryFromDto() {
        CategoryCreateRequestDto dto = new CategoryCreateRequestDto();
        dto.setName("SUV");

        CategoryResponseDto createdCategory = categoryService.createCategory(dto);

        assertNotNull(createdCategory);
        assertEquals("SUV", createdCategory.getName());
    }

    @Test
    public void update_shouldUpdateCategoryName_whenCategoryExists() {

        Category existingCategory = createCategory();
        Long categoryId = existingCategory.getId();

        CategoryUpdateRequestDto dto = new CategoryUpdateRequestDto();
        dto.setName("New Name");

        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(existingCategory));

        categoryService.update(categoryId, dto);

        assertEquals("New Name", existingCategory.getName());
        verify(categoryRepository).findById(categoryId);
    }

    @Test
    public void findAll_shouldReturnPagedCategories() {
        Pageable pageable = Pageable.ofSize(10);
        Page<Category> categoryPage = new PageImpl<>(List.of(createCategory()));
        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);

        Page<Category> result = categoryService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }
}

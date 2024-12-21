package com.foxminded.korniichyk.car_rest_service.controller;

import com.foxminded.korniichyk.car_rest_service.dto.category.CategoryCreateRequestDto;
import com.foxminded.korniichyk.car_rest_service.dto.category.CategoryResponseDto;
import com.foxminded.korniichyk.car_rest_service.dto.category.CategoryUpdateRequestDto;
import com.foxminded.korniichyk.car_rest_service.mapper.category.CategoryResponseMapper;
import com.foxminded.korniichyk.car_rest_service.model.Category;
import com.foxminded.korniichyk.car_rest_service.service.impl.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static com.foxminded.korniichyk.car_rest_service.controller.constant.ControllerConstant.ASC_SORT_ORDER_OPTION;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/categories")
@Tag(
        name = "Category controller",
        description = "Here we have endpoints for managing categories."
)
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryResponseMapper categoryResponseMapper;

    @Operation(
            summary = "Get categories",
            description = "Get page of categories",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Page of categories successfully retrieved",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request parameters",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @GetMapping
    public PagedModel<EntityModel<CategoryResponseDto>> getCategories(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = ASC_SORT_ORDER_OPTION) String direction,
            PagedResourcesAssembler<CategoryResponseDto> assembler
    ) {

        Sort sort;
        if (direction.equals(ASC_SORT_ORDER_OPTION)) {
            sort = Sort.by(sortBy);
        } else {
            sort = Sort.by(Sort.Order.desc(sortBy));
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CategoryResponseDto> responsePage = categoryService.findAll(pageable).map(categoryResponseMapper::toDto);

        return assembler.toModel(responsePage);
    }


    @GetMapping("/{id}")
    @Operation(
            summary = "Get category",
            description = "Get category by id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Category successfully retrieved",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Category not found",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    public CategoryResponseDto getCategory(
            @PathVariable Long id
    ) {
        Category category = categoryService.findById(id);
        return categoryResponseMapper.toDto(category);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            summary = "Create category",
            description = "Only authenticated users can create category",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Category created successfully.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CategoryResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Invalid category id",
                            content = @Content(mediaType = "application/json")
                    ),


                    @ApiResponse(responseCode = "401",
                            description = "Authentication required",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    public CategoryResponseDto createCategory(
            @RequestBody @Valid CategoryCreateRequestDto categoryCreateRequestDto
    ) {
        return categoryService.createCategory(categoryCreateRequestDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            summary = "Delete category",
            description = "Only authenticated users can delete category",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Category successfully deleted",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Invalid category id. category not found",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Authentication required",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    public void deleteCategory(
            @PathVariable Long id
    ) {
        categoryService.delete(id);
    }

    @PutMapping("/{id}")
    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            summary = "Update category",
            description = "Only authenticated users can update category.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Category created successfully.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CategoryResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Invalid category id",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request parameters",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Authentication required",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    public CategoryResponseDto updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryUpdateRequestDto categoryUpdateRequestDto
    ) {
        return categoryService.update(id, categoryUpdateRequestDto);
    }

}

package com.foxminded.korniichyk.car_rest_service.controller;

import com.foxminded.korniichyk.car_rest_service.dto.brand.BrandCreateRequestDto;
import com.foxminded.korniichyk.car_rest_service.dto.brand.BrandResponseDto;
import com.foxminded.korniichyk.car_rest_service.dto.brand.BrandUpdateRequestDto;
import com.foxminded.korniichyk.car_rest_service.mapper.brand.BrandResponseMapper;
import com.foxminded.korniichyk.car_rest_service.model.Brand;
import com.foxminded.korniichyk.car_rest_service.service.impl.BrandService;
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
@RequestMapping("/api/v1/brands")
@Tag(
        name = "Brand controller",
        description = "Here we have endpoints for managing brands."
)
public class BrandController {

    private final BrandService brandService;
    private final BrandResponseMapper brandResponseMapper;
    private final PagedResourcesAssembler<BrandResponseDto> assembler;

    @GetMapping()
    @Operation(
            summary = "Get brands",
            description = "Get page of brands",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Page of brands successfully retrieved",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request parameters",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    public PagedModel<EntityModel<BrandResponseDto>> getBrands(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = ASC_SORT_ORDER_OPTION) String direction
    ) {

        Sort sort;
        if (direction.equals(ASC_SORT_ORDER_OPTION)) {
            sort = Sort.by(sortBy);
        } else {
            sort = Sort.by(Sort.Order.desc(sortBy));
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<BrandResponseDto> brandPage = brandService.findAll(pageable).map(brandResponseMapper::toDto);

        return assembler.toModel(brandPage);
    }


    @GetMapping("/{id}")
    @Operation(
            summary = "Get brand",
            description = "Get brand by id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Brand successfully retrieved",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Brand not found",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    public BrandResponseDto getBrand(
            @PathVariable Long id
    ) {
        Brand brand = brandService.findById(id);
        return brandResponseMapper.toDto(brand);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            summary = "Create Brand",
            description = "Only authenticated users can create brand",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Brand created successfully.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BrandResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Brand not found",
                            content = @Content(mediaType = "application/json")
                    ),


                    @ApiResponse(responseCode = "401",
                            description = "Authentication required",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    public BrandResponseDto createBrand(
            @RequestBody @Valid BrandCreateRequestDto createBrandRequestDto
    ) {
        return brandService.createBrand(createBrandRequestDto);

    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            summary = "Delete brand",
            description = "Only authenticated users can delete brand",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Brand successfully deleted",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Invalid brand id. Brand not found",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Authentication required",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )

    public void deleteBrand(
            @PathVariable Long id
    ) {
        brandService.delete(id);
    }

    @PutMapping("/{id}")
    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            summary = "Update brand",
            description = "Only authenticated users can update brand.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Brand created successfully.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BrandResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Invalid brand id",
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
    public BrandResponseDto updateBrand(
            @PathVariable Long id,
            @RequestBody @Valid BrandUpdateRequestDto brandUpdateRequestDto
    ) {
        return brandService.update(id, brandUpdateRequestDto);
    }

}

package com.foxminded.korniichyk.car_rest_service.controller;

import com.foxminded.korniichyk.car_rest_service.dto.model.ModelCreateRequestDto;
import com.foxminded.korniichyk.car_rest_service.dto.model.ModelResponseDto;
import com.foxminded.korniichyk.car_rest_service.dto.model.ModelUpdateRequestDto;
import com.foxminded.korniichyk.car_rest_service.mapper.model.ModelResponseMapper;
import com.foxminded.korniichyk.car_rest_service.model.Model;
import com.foxminded.korniichyk.car_rest_service.service.impl.ModelService;
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
@RequestMapping("/api/v1/models")
@Tag(
        name = "Model controller",
        description = "Here we have endpoints for managing models."
)
public class ModelController {

    private final ModelService modelService;
    private final ModelResponseMapper modelResponseMapper;


    @GetMapping()
    @Operation(
            summary = "Get models",
            description = "Get page of models",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Page of models successfully retrieved",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request parameters",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    public PagedModel<EntityModel<ModelResponseDto>> getModels(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = ASC_SORT_ORDER_OPTION) String direction,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String generation,
            @RequestParam(required = false) String brand,
            PagedResourcesAssembler<ModelResponseDto> assembler
    ) {
        Sort sort;
        if (direction.equals(ASC_SORT_ORDER_OPTION)) {
            sort = Sort.by(sortBy);
        } else {
            sort = Sort.by(Sort.Order.desc(sortBy));
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ModelResponseDto> responsePage = modelService.findAll(pageable, name, generation, brand);

        return assembler.toModel(responsePage);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get model",
            description = "Get model by id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Model successfully retrieved",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Model not found",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    public ModelResponseDto getModel(
            @PathVariable Long id
    ) {
        Model model = modelService.findById(id);
        return modelResponseMapper.toDto(model);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            summary = "Create model",
            description = "Only authenticated users can create engine",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Model created successfully.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ModelResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Model not found",
                            content = @Content(mediaType = "application/json")
                    ),


                    @ApiResponse(responseCode = "401",
                            description = "Authentication required",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    public ModelResponseDto createModel(
            @RequestBody @Valid ModelCreateRequestDto modelCreateRequestDto
    ) {
        return modelService.createModel(modelCreateRequestDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            summary = "Delete model",
            description = "Only authenticated users can delete model",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Model successfully deleted",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Model not found",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Authentication required",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    public void deleteModel(@PathVariable Long id) {
        modelService.delete(id);
    }

    @PutMapping("/{id}")
    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            summary = "Update model",
            description = "Only authenticated users can update model.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Model created successfully.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ModelResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Model not found",
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
    public ModelResponseDto updateModel(
            @PathVariable(name = "id") Long id,
            @Valid @RequestBody ModelUpdateRequestDto modelUpdateRequestDto
    ) {
       return modelService.update(id, modelUpdateRequestDto);
    }



}

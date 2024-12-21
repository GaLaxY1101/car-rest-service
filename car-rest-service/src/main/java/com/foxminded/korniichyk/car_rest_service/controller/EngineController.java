package com.foxminded.korniichyk.car_rest_service.controller;

import com.foxminded.korniichyk.car_rest_service.dto.engine.EngineCreateRequestDto;
import com.foxminded.korniichyk.car_rest_service.dto.engine.EngineResponseDto;
import com.foxminded.korniichyk.car_rest_service.dto.engine.EngineUpdateRequestDto;
import com.foxminded.korniichyk.car_rest_service.mapper.engine.EngineResponseMapper;
import com.foxminded.korniichyk.car_rest_service.model.Engine;
import com.foxminded.korniichyk.car_rest_service.service.impl.EngineService;
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
@RestController()
@RequestMapping("/api/v1/engines")
@Tag(
        name = "Engine controller",
        description = "Here we have endpoints for managing engines."
)
public class EngineController {

    private final EngineService engineService;
    private final EngineResponseMapper engineResponseMapper;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping()
    @Operation(
            summary = "Get engines",
            description = "Get page of engines",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Page of engines successfully retrieved",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request parameters",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    public PagedModel<EntityModel<EngineResponseDto>> getEngines(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = ASC_SORT_ORDER_OPTION) String direction,
            PagedResourcesAssembler<EngineResponseDto> assembler
    ) {

        Sort sort;
        if (direction.equals(ASC_SORT_ORDER_OPTION)) {
            sort = Sort.by(sortBy);
        } else {
            sort = Sort.by(Sort.Order.desc(sortBy));
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<EngineResponseDto> responsePage =  engineService.findAll(pageable).map(engineResponseMapper::toDto);

        return assembler.toModel(responsePage);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get engine",
            description = "Get engine by id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Engine successfully retrieved",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Engine not found",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    public EngineResponseDto getEngine(
            @PathVariable Long id
    ) {
        Engine engine = engineService.findById(id);
        return engineResponseMapper.toDto(engine);
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            summary = "Create engine",
            description = "Only authenticated users can create engine",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Engine created successfully.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = EngineResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Engine not found",
                            content = @Content(mediaType = "application/json")
                    ),


                    @ApiResponse(responseCode = "401",
                            description = "Authentication required",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    public EngineResponseDto createEngine(
            @RequestBody @Valid EngineCreateRequestDto engineCreateRequestDto
    ) {
        return engineService.createEngine(engineCreateRequestDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            summary = "Delete engine",
            description = "Only authenticated users can delete engine",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Engine successfully deleted",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Engine not found",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Authentication required",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    public void deleteEngine(
            @PathVariable Long id
    ) {
        engineService.delete(id);
    }

    @PutMapping("/{id}")
    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            summary = "Update engine",
            description = "Only authenticated users can update engine.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Engine created successfully.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = EngineResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Engine not found",
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
    public EngineResponseDto updateEngine(
            @PathVariable Long id,
            @RequestBody @Valid EngineUpdateRequestDto engineUpdateResponseDto
    ) {
        return engineService.update(id, engineUpdateResponseDto);
    }

}

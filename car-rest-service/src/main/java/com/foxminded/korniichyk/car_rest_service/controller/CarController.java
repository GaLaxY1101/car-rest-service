package com.foxminded.korniichyk.car_rest_service.controller;

import com.foxminded.korniichyk.car_rest_service.dto.car.CarCreateRequestDto;
import com.foxminded.korniichyk.car_rest_service.dto.car.CarResponseDto;
import com.foxminded.korniichyk.car_rest_service.dto.car.CarUpdateRequestDto;
import com.foxminded.korniichyk.car_rest_service.mapper.car.CarResponseMapper;
import com.foxminded.korniichyk.car_rest_service.model.Car;
import com.foxminded.korniichyk.car_rest_service.service.impl.CarService;
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
@RequestMapping("/api/v1/cars")
@Tag(
        name = "Car controller",
        description = "Here we have endpoints for managing cars."
)
public class CarController {

    private final CarService carService;
    private final CarResponseMapper carResponseMapper;

    @GetMapping
    @Operation(
            summary = "Get cars",
            description = "Get page of cars",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Page of cars successfully retrieved",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request parameters",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    public PagedModel<EntityModel<CarResponseDto>> getCars(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "modelName") String sortBy,
            @RequestParam(defaultValue = ASC_SORT_ORDER_OPTION) String direction,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String yearOfManufacturingFrom,
            @RequestParam(required = false) String yearOfManufacturingTill,
            PagedResourcesAssembler<CarResponseDto> assembler
    ) {


        if (sortBy.equals("modelName")) {
            sortBy = "model.name";
        }

        Sort sort;
        if (direction.equals(ASC_SORT_ORDER_OPTION)) {
            sort = Sort.by(sortBy);
        } else {
            sort = Sort.by(Sort.Order.desc(sortBy));
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CarResponseDto> responsePage = carService.findAll(
                pageable,
                model,
                yearOfManufacturingFrom,
                yearOfManufacturingTill,
                category
        );

        return assembler.toModel(responsePage);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get car",
            description = "Get var by id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Car successfully retrieved",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Invalid car id. Car not found",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    public CarResponseDto getCar(
            @PathVariable Long id
    ) {
        Car car = carService.findById(id);
        return carResponseMapper.toDto(car);
    }

    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            summary = "Create car",
            description = "Only authenticated users can create car",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Car created successfully.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CarResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Invalid car id",
                            content = @Content(mediaType = "application/json")
                    ),


                    @ApiResponse(responseCode = "401",
                            description = "Authentication required",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CarResponseDto createCar(
            @RequestBody @Valid CarCreateRequestDto carCreateRequestDto
    ) {
        return carService.createCar(carCreateRequestDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            summary = "Delete car",
            description = "Only authenticated users can delete car",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Car successfully deleted",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Car not found",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Authentication required",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    public void deleteCars(
            @PathVariable Long id
    ) {
        carService.delete(id);
    }

    @PutMapping("/{id}")
    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            summary = "Update car",
            description = "Only authenticated users can update car.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Car created successfully.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CarResponseDto.class)
                            )),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Invalid car id",
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
    public CarResponseDto updateCar(
            @PathVariable Long id,
            @RequestBody @Valid CarUpdateRequestDto carUpdateRequestDto
    ) {
        return carService.update(id, carUpdateRequestDto);
    }
}

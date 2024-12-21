package com.foxminded.korniichyk.car_rest_service.dto.model;

import com.foxminded.korniichyk.car_rest_service.model.Brand;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Data
public class ModelCreateRequestDto {

    @NotEmpty(message = "Name shouldn't be empty")
    private String name;

    @NotEmpty(message = "Start manufacturing date shouldn't be empty")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z$",
            message = "Start manufacturing date must be in ISO-8601 format (yyyy-MM-ddTHH:mm:ssZ)")
    private String startManufacturing;

    @NotEmpty(message = "End manufacturing date shouldn't be empty")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z$",
            message = "End manufacturing date must be in ISO-8601 format (yyyy-MM-ddTHH:mm:ssZ)")
    private String endManufacturing;

    @NotEmpty(message = "Generation shouldn't be empty")
    private String generation;

    @NotNull(message = "Brand shouldn't be empty")
    private Long brandId;
}

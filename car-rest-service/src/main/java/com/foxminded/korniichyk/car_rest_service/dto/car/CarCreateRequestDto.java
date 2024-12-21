package com.foxminded.korniichyk.car_rest_service.dto.car;

import com.foxminded.korniichyk.car_rest_service.model.Car;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CarCreateRequestDto {

    @NotEmpty(message = "Color shouldn't be empty")
    private String color;

    @NotEmpty(message = "Serial number shouldn't be empty")
    private String serialNumber;

    @NotNull(message = "Category shouldn't be empty")
    private Long categoryId;

    @NotNull(message = "Model shouldn't be empty")
    private Long modelId;

    @NotEmpty(message = "Manufacturing date shouldn't be empty")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z$",
            message = "Start manufacturing date must be in ISO-8601 format (yyyy-MM-ddTHH:mm:ssZ)")
    private String manufacturingDate;

    @NotNull(message = "Engine shouldn't be empty")
    private Long engineId;

    @NotNull(message = "Drive shouldn't be empty")
    private Car.Drive drive;
}

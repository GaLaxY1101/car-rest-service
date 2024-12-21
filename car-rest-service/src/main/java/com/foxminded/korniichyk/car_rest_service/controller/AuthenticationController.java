package com.foxminded.korniichyk.car_rest_service.controller;

import com.foxminded.korniichyk.car_rest_service.dto.auth.AuthenticationRequestDto;
import com.foxminded.korniichyk.car_rest_service.dto.auth.AuthenticationResponseDto;
import com.foxminded.korniichyk.car_rest_service.dto.auth.RegisterRequestDto;
import com.foxminded.korniichyk.car_rest_service.security.Auth0Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(
        name = "Authentication controller",
        description = "Here we have endpoints for login and register"
)
public class AuthenticationController {

    private final Auth0Service authenticationService;

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Registers a new user by providing necessary registration details.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User registration details",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RegisterRequestDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User successfully registered",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request data",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    public String register(
            @RequestBody @Valid RegisterRequestDto registerRequestDto
    ) {
        return authenticationService.register(registerRequestDto);
    }

    @PostMapping("/authenticate")
    @Operation(
            summary = "Authenticate a user",
            description = "Authenticates a user and returns a JWT token if credentials are valid.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User authentication details",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationRequestDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User authenticated successfully",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid credentials",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    public AuthenticationResponseDto authenticate(
            @RequestBody @Valid AuthenticationRequestDto authenticationRequestDto
    ) {
        return authenticationService.authenticate(authenticationRequestDto);
    }

}

package com.foxminded.korniichyk.car_rest_service.advice;

import com.foxminded.korniichyk.car_rest_service.dto.ErrorResponseDto;
import com.foxminded.korniichyk.car_rest_service.exception.AuthenticationFailedException;
import com.foxminded.korniichyk.car_rest_service.exception.FailToCheckUserExistenceException;
import com.foxminded.korniichyk.car_rest_service.exception.RegistrationFailedException;
import com.foxminded.korniichyk.car_rest_service.exception.UserWithProvidedEmailAlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponseDto handleResourceNotFound(EntityNotFoundException ex) {
        return new ErrorResponseDto(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value()
        );
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponseDto handleValidationError(MethodArgumentNotValidException ex) {
        List<String> errorMessages = ex.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .toList();

        return new ErrorResponseDto(
                errorMessages.toString(),
                HttpStatus.BAD_REQUEST.value());

    }

    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ErrorResponseDto handleOptimisticLockException(OptimisticLockingFailureException ex) {
        return new ErrorResponseDto(
                "The resource has been modified by another user. Please provide the latest version.",
                HttpStatus.CONFLICT.value()
        );
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AuthenticationFailedException.class)
    public ErrorResponseDto handleAuthenticationFailedException(AuthenticationFailedException ex) {
        return new ErrorResponseDto(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value()
        );
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(UserWithProvidedEmailAlreadyExistsException.class)
    public ErrorResponseDto handle(UserWithProvidedEmailAlreadyExistsException ex) {
        return new ErrorResponseDto(
                ex.getMessage(),
                HttpStatus.CONFLICT.value()
        );
    }


    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RegistrationFailedException.class)
    public ErrorResponseDto handleRegistrationFailedException(Exception ex) {
        return new ErrorResponseDto(
                "Failed to register user. Please, try again later",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class, FailToCheckUserExistenceException.class})
    public ErrorResponseDto handleGenericException(Exception ex) {
        return new ErrorResponseDto(
                "An unexpected error occurred. Please try again later",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
    }

}

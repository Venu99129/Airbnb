package com.sourceCode.Airbnb.advice;

import com.sourceCode.Airbnb.exceptions.IntegerFormatException;
import com.sourceCode.Airbnb.exceptions.ResourceNotFoundException;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> resourceNotFountExceptionHandler(ResourceNotFoundException exception){
        ApiError apiError = ApiError.builder()
                .message(exception.getMessage())
                .status(HttpStatus.NOT_FOUND).build();
        return new ResponseEntity<>(new ApiResponse<>(apiError) , apiError.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException exception){
        List<String> subErrors = exception.getBindingResult()
                .getAllErrors()
                .stream().map(error-> error.getDefaultMessage())
                .toList();

        ApiError apiError = ApiError.builder().message("given parameters are not valid")
                .subErrors(subErrors).status(HttpStatus.BAD_REQUEST).build();

        return new ResponseEntity<>(new ApiResponse<>(apiError), apiError.getStatus());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException exception){
        List<String> subErrors = List.of(exception.getLocalizedMessage());

        ApiError apiError = ApiError.builder()
                .message("given json format is not valid format")
                .subErrors(subErrors)
                .status(HttpStatus.BAD_REQUEST)
                .build();
        return new ResponseEntity<>(new ApiResponse<>(apiError), apiError.getStatus());
    }

    @ExceptionHandler(IntegerFormatException.class)
    public ResponseEntity<ApiResponse<?>> integerFormatExceptionHandler(IntegerFormatException exception){
        List<String> subErrors = List.of(exception.getMessage());
        ApiError apiError = ApiError.builder()
                .message("we except only integers")
                .subErrors(subErrors)
                .status(HttpStatus.BAD_REQUEST)
                .build();
        return new ResponseEntity<>(new ApiResponse<>(apiError), apiError.getStatus());
    }

    @ExceptionHandler(PSQLException.class)
    public ResponseEntity<ApiResponse<?>> psqlExceptionHandler(PSQLException exception){
        ApiError apiError = ApiError.builder()
                .message(exception.getMessage())
                .status(HttpStatus.NOT_FOUND).build();
        return new ResponseEntity<>(new ApiResponse<>(apiError) , apiError.getStatus());
    }
}

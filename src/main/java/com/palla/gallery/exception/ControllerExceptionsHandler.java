package com.palla.gallery.exception;

import com.palla.gallery.dto.ErrorDetailsDto;
import io.jsonwebtoken.MalformedJwtException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ControllerExceptionsHandler {

    @ExceptionHandler({InvalidCredentialsException.class})
    public ResponseEntity<?> handleInvalidCredentialsExceptions(InvalidCredentialsException ex, WebRequest request) {
        ErrorDetailsDto errorDetails = new ErrorDetailsDto(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<?> handleUserNotFoundExceptions(UserNotFoundException ex, WebRequest request) {
        ErrorDetailsDto errorDetails = new ErrorDetailsDto(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ImageNotFoundException.class})
    public ResponseEntity<?> handleImageNotFoundExceptions(ImageNotFoundException ex, WebRequest request) {
        ErrorDetailsDto errorDetails = new ErrorDetailsDto(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({IOException.class})
    public ResponseEntity<?> handleIOExceptions(IOException ex, WebRequest request) {
        ErrorDetailsDto errorDetails = new ErrorDetailsDto(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<?> handleConstraintViolationExceptions(ConstraintViolationException ex, WebRequest request) {
        ErrorDetailsDto errorDetails = new ErrorDetailsDto(new Date(), "Username is already exist", request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<?> handleAuthenticationExceptions(AuthenticationException ex, WebRequest request) {
        ErrorDetailsDto errorDetails = new ErrorDetailsDto(new Date(), "Authentication failed due to invalid credentials/token", request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MalformedJwtException.class})
    public ResponseEntity<?> handleJwtExceptions(MalformedJwtException ex, WebRequest request) {
        ErrorDetailsDto errorDetails = new ErrorDetailsDto(new Date(), "Invalid token", request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

}

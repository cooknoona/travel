package com.travel.exception;

import com.travel.exception.client.*;
import com.travel.exception.server.BadGatewayException;
import com.travel.exception.server.InternalServerErrorException;
import com.travel.exception.server.ServiceUnavailableException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * Global Exception Handler is in order to catch various exceptions. It'll throw exceptions in Service layer.
 * Once implementations fail, and also it will give a user error response via error response */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errorMessages = new ArrayList<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errorMessages.add(fieldError.getField() + ": " + fieldError.getDefaultMessage());
        }

        ErrorResponse errorResponse = new ErrorResponse(400, "Validation failed", errorMessages);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(400, "Bad Request", errorMessages);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException e) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(401, "Bad Credentials", errorMessages);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleUnauthorisedException(UnauthenticatedException e) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(401, "Unauthorized", errorMessages);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(403, "Forbidden", errorMessages);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler({EntityNotFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundExceptions(RuntimeException e) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(404, "Not Found", errorMessages);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleMethodNotAllowedException(MethodNotAllowedException e) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(405, "Method Not Allowed", errorMessages);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleConflictException(DuplicateKeyException e) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(409, "Conflict", errorMessages);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleUnprocessableEntityException(UnprocessableEntityException e) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(422, "Unprocessable Entity", errorMessages);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleLockedException(LockedException e) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(423, "Locked", errorMessages);
        return ResponseEntity.status(HttpStatus.LOCKED).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleInternalServerErrorException(InternalServerErrorException e) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(500, "Internal Server Error", errorMessages);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleBadGatewayException(BadGatewayException e) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(502, "Bad Gateway", errorMessages);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleServiceUnavailableException(ServiceUnavailableException e) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(503, "Service Unavailable", errorMessages);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }
}

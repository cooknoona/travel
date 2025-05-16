package com.travel.exception;

import com.travel.exception.client.*;
import com.travel.exception.server.BadGatewayException;
import com.travel.exception.server.ServiceUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

/**
 * Global Exception Handler is in order to catch various exceptions. It'll throw exceptions in Service layer or Business layer.
 * Once implementations fail, and also it will give a user error response via error response
 * However, the MethodArgumentNotValidException is Internal logic exception, In controller layer due to @Valid will catch this error
 * and throw this instead. Basically the way I implemented custom exception classed are for business logic */
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

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(400, "Bad Request", errorMessages);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException e) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(401, "Bad Credentials", errorMessages);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(UnauthenticatedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorisedException(UnauthenticatedException e) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(401, "Unauthorized", errorMessages);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(com.travel.exception.client.AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(403, "Forbidden", errorMessages);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundExceptions(ResourceNotFoundException e) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(404, "Not Found", errorMessages);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(MethodNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowedException(MethodNotAllowedException e) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(405, "Method Not Allowed", errorMessages);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(ConflictException e) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(409, "Conflict", errorMessages);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(UnprocessableEntityException.class)
    public ResponseEntity<ErrorResponse> handleUnprocessableEntityException(UnprocessableEntityException e) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(422, "Unprocessable Entity", errorMessages);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ErrorResponse> handleLockedException(LockedException e) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(423, "Locked", errorMessages);
        return ResponseEntity.status(HttpStatus.LOCKED).body(errorResponse);
    }

    /** If an exception is not handled and propagates up to the top level, Spring will automatically respond with a 500 Internal Server Error.
     *  This GlobalExceptionHandler will catch Exception which is the top level of exception */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleInternalServerErrorException(Exception e) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(500, "Internal Server Error", errorMessages);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /** External API call exceptions, So these two 502 and 503 are refer custom exception class */
    @ExceptionHandler(BadGatewayException.class)
    public ResponseEntity<ErrorResponse> handleBadGatewayException(BadGatewayException e) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(502, "Bad Gateway", errorMessages);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(errorResponse);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleServiceUnavailableException(ServiceUnavailableException e) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(503, "Service Unavailable", errorMessages);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }
}

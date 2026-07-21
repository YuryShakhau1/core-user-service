package by.shakhau.core.user.controller;

import by.shakhau.core.user.controller.dto.response.ErrorResponse;
import by.shakhau.core.user.service.exception.ResourceForbiddenException;
import by.shakhau.core.user.service.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException exception, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, exception, request);
    }

    @ExceptionHandler(ResourceForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleResourceForbiddenException(
            ResourceForbiddenException exception, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, exception, request);
    }

    @ExceptionHandler(MethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleMethodValidationException(
            MethodValidationException exception, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, exception, request);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            HttpStatus status, Exception exception, HttpServletRequest request) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(exception.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(errorResponse);
    }
}

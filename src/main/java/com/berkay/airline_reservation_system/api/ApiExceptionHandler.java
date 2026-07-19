package com.berkay.airline_reservation_system.api;

import com.berkay.airline_reservation_system.dto.ApiError;
import com.berkay.airline_reservation_system.exception.NotFoundException;
import com.berkay.airline_reservation_system.exception.SeatUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice(basePackages = "com.berkay.airline_reservation_system.api")
public class ApiExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex) {

        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // map more than one exception type to one method to handle both.
    @ExceptionHandler({SeatUnavailableException.class, ObjectOptimisticLockingFailureException.class})
    public ResponseEntity<ApiError> handleConflict(Exception e) {
        return build(HttpStatus.CONFLICT, "Seat is no longer available!");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        return build(HttpStatus.BAD_REQUEST, "Invalid request body!");
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String message) {

        ApiError body = new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message);
        return ResponseEntity.status(status).body(body);
    }
}

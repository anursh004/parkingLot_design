package com.parkinglot.exception;

import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({InvalidVehicleException.class, OtpException.class})
    public ResponseEntity<ApiError> handleBadRequest(RuntimeException ex) {
        logger.warn("Validation error: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage(), Instant.now()));
    }

    @ExceptionHandler({ParkingLotFullException.class})
    public ResponseEntity<ApiError> handleCapacity(RuntimeException ex) {
        logger.warn("Capacity issue: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError(ex.getMessage(), Instant.now()));
    }

    @ExceptionHandler({TicketNotFoundException.class})
    public ResponseEntity<ApiError> handleNotFound(RuntimeException ex) {
        logger.warn("Ticket not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(ex.getMessage(), Instant.now()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex) {
        logger.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError("Unexpected error", Instant.now()));
    }
}

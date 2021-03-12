package com.billable.photostorage.security.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
public class RestExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity vehicleNotFound(HttpClientErrorException e) {
        log.info("Token is invalid... {}", e.getMessage());
        return ResponseEntity.noContent().build();
    }
}

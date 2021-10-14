package com.tbarauskas.elastumtask.exception;

import com.tbarauskas.elastumtask.model.ErrorHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PersonNotFoundException.class)
    public ResponseEntity<ErrorHandler> exceptionHandler(PersonNotFoundException e) {
        log.debug("Person with id - {} is not found", e.getId());
        return new ResponseEntity<>(new ErrorHandler(HttpStatus.NOT_FOUND.value(),
                String.format("Person with id - %d is not found", e.getId())), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorHandler> exceptionHandler(Exception e) {
        log.error("Unexpected error - {}", e.getMessage());
        e.printStackTrace();
        return new ResponseEntity<>(new ErrorHandler(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server error"),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

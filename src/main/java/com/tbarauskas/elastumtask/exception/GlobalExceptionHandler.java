package com.tbarauskas.elastumtask.exception;

import com.tbarauskas.elastumtask.model.ErrorHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    @ExceptionHandler(MoreThenOneAllowedSymbolInNameException.class)
    public ResponseEntity<ErrorHandler> exceptionHandler(MoreThenOneAllowedSymbolInNameException e) {
        log.debug("Invalid name entered for creating Person");
        return new ResponseEntity<>(new ErrorHandler(HttpStatus.BAD_REQUEST.value(),
                "Name can't be more then two words and no spacing allowed in front or end of name"),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoRelativeFindForCurrentPersonException.class)
    public ResponseEntity<ErrorHandler> exceptionHandler(NoRelativeFindForCurrentPersonException e){
        return new ResponseEntity<>(new ErrorHandler(HttpStatus.BAD_REQUEST.value(), "No relative found"),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MoreThenOneAllowedSymbolInSurnameException.class)
    public ResponseEntity<ErrorHandler> exceptionHandler(MoreThenOneAllowedSymbolInSurnameException e) {
        log.debug("Invalid surname entered for creating Person");
        return new ResponseEntity<>(new ErrorHandler(HttpStatus.BAD_REQUEST.value(),
                "Surname can't be more then two words and no '-' symbol allowed in front or end of surname"),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorHandler> exceptionHandler(HttpMessageNotReadableException e) {
        log.debug("Bad date format entered");
        return new ResponseEntity<>(new ErrorHandler(HttpStatus.BAD_REQUEST.value(),
                "Date format must be - 'yyyy-MM-dd' "), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorHandler> exceptionHandler(MethodArgumentNotValidException e) {
        return new ResponseEntity<>(new ErrorHandler(HttpStatus.BAD_REQUEST.value(),
                e.getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .collect(java.util.stream.Collectors.joining(", \n"))),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorHandler> exceptionHandler(Exception e) {
        log.error("Unexpected error - {}", e.getMessage());
        e.printStackTrace();
        return new ResponseEntity<>(new ErrorHandler(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server error"),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

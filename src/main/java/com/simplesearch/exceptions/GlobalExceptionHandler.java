package com.simplesearch.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ErrorMessage> resourceNotFoundException(Exception ex) {
        ex.printStackTrace();
        ErrorMessage message = new ErrorMessage(500, new Date(), ex.getMessage(), "");
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
}

package com.cringee.simplescreensharing.handlers;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class ValidateExceptionsHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleArgMismatch(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

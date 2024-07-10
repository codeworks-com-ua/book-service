package com.service_book.demo.controller.advice;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.service_book.demo.exception.AuthenticationException;
import com.service_book.demo.exception.DataNotFoundException;
import com.service_book.demo.exception.GenericProblem;

@RestControllerAdvice
public class ExceptionAdviceService {

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<?> handleDataNotFound(DataNotFoundException e) {
        return ResponseEntity
                .status(NOT_FOUND)
                .body(GenericProblem.builder()
                        .details(e.getMessage())
                        .build());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException e) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(GenericProblem.builder()
                        .details(e.getMessage())
                        .build());
    }
}

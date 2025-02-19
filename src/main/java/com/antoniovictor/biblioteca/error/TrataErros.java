package com.antoniovictor.biblioteca.error;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Component
@RestControllerAdvice
public class TrataErros {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> notFound() {
        return ResponseEntity.notFound().build();
    }
}

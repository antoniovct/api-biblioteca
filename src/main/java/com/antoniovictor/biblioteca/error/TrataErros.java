package com.antoniovictor.biblioteca.error;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Component
@RestControllerAdvice
public class TrataErros {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> notFound() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity validation(MethodArgumentNotValidException e) {
        var fieldErrors = e.getFieldErrors().stream().map(ErrosDto::new).toList();
        return ResponseEntity.badRequest().body(fieldErrors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity badRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    private record ErrosDto(
            String msg,
            String field
    ) {
        public ErrosDto(FieldError fieldError) {
            this(fieldError.getDefaultMessage(), fieldError.getField());
        }
    }




}

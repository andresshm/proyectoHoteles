package com.proyecto.hoteles.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.proyecto.hoteles.common.StandarizedApiExceptionResponse;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleUnknownException(Exception ex){
        StandarizedApiExceptionResponse saer = new StandarizedApiExceptionResponse("TECNICO", "I/O error", "500", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(saer);
    }


    @ExceptionHandler(BussinesRuleException.class)
    public ResponseEntity<?> handleBussinesRuleException(BussinesRuleException ex){
        StandarizedApiExceptionResponse saer = new StandarizedApiExceptionResponse("E/S", ex.getTitle(), ex.getCode(), ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus()).body(saer);
    }
}

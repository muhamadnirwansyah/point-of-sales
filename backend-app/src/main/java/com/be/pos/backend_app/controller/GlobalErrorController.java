package com.be.pos.backend_app.controller;

import com.be.pos.backend_app.exception.ConflictException;
import com.be.pos.backend_app.exception.NotfoundException;
import com.be.pos.backend_app.model.ApiResponse;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.List;

@Slf4j
@Hidden
@RestControllerAdvice
public class GlobalErrorController {

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse> conflictException(ConflictException e){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.failed(
                        Collections.singletonList(e.getMessage()),
                        HttpStatus.CONFLICT.value()
                ));
    }

    @ExceptionHandler(NotfoundException.class)
    public ResponseEntity<ApiResponse> notfoundException(NotfoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.failed(
                        Collections.singletonList(e.getMessage()),
                        HttpStatus.NOT_FOUND.value()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> requestInvalidException(MethodArgumentNotValidException e){

        List<String> defaultMessages = e.getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failed(
                        defaultMessages,
                        HttpStatus.BAD_REQUEST.value()
                ));
    }
}

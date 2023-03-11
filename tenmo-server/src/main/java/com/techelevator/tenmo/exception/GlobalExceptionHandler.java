package com.techelevator.tenmo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = TransferNotFound.class)
    public ResponseEntity<Response> handleTransferNotFound(TransferNotFound e){
        String message = e.getMessage();
        Response response = new Response (message, LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UserNotFound.class)
    public ResponseEntity<Response> handleUserNotFound(UserNotFound e){
        String message = e.getMessage();
        Response response = new Response (message, LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = AccountNotFound.class)
    public ResponseEntity<Response> handleAccountNotFound(AccountNotFound e){
        String message = e.getMessage();
        Response response = new Response (message, LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}

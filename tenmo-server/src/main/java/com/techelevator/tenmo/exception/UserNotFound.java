package com.techelevator.tenmo.exception;

public class UserNotFound extends RuntimeException{
    public UserNotFound(String message) {
        super(message);
    }
}

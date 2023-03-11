package com.techelevator.tenmo.exception;

public class AccountNotFound extends RuntimeException{
    public AccountNotFound(String message) {
        super(message);
    }
}

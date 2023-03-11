package com.techelevator.tenmo.exception;

public class TransferNotFound extends RuntimeException{
    public TransferNotFound(String message) {
        super(message);
    }
}

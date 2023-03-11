package com.techelevator.tenmo.exception;

import java.time.LocalDateTime;

public class Response {
    private final String message;
    private final LocalDateTime timeStamp;

    public Response(String message, LocalDateTime timeStamp) {
        this.message = message;
        this.timeStamp = timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }
}

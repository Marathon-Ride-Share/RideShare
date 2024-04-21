package com.marathonrideshare.rideshare.exceptions;

public class RideShareException extends Exception{
    private final int statusCode;

    public RideShareException(int statusCode, String errorMessage) {
        super(errorMessage);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

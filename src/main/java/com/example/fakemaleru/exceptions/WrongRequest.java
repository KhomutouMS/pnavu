package com.example.fakemaleru.exceptions;

public class WrongRequest extends RuntimeException {
    public WrongRequest(String message) {
        super(message);
    }
}

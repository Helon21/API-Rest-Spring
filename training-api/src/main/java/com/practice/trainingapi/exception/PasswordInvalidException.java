package com.practice.trainingapi.exception;

public class PasswordInvalidException extends RuntimeException {
    public PasswordInvalidException(String msg) {
        super(msg);
    }
}

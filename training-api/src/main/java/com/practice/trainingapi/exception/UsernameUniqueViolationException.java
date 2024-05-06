package com.practice.trainingapi.exception;

public class UsernameUniqueViolationException extends RuntimeException {

    public UsernameUniqueViolationException(String msg) {
        super(msg);
    }

}

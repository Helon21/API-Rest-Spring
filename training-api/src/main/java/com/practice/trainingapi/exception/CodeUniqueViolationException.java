package com.practice.trainingapi.exception;

public class CodeUniqueViolationException extends RuntimeException {
    public CodeUniqueViolationException(String msg) {
        super(msg);
    }
}

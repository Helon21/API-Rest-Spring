package com.practice.trainingapi.exception;

public class CpfUniqueViolationException extends RuntimeException {

    public CpfUniqueViolationException(String msg) {
        super(msg);
    }
}

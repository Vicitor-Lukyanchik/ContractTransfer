package com.example.transfer.dbf.exception;

public class ProcessException extends RuntimeException {

    public ProcessException(String message) {
        super(message);
    }
    public ProcessException(String message, Exception e) {
        super(message, e);
    }
}

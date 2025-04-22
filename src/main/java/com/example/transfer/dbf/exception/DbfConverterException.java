package com.example.transfer.dbf.exception;

public class DbfConverterException extends RuntimeException {

    public DbfConverterException(String message) {
        super(message);
    }
    public DbfConverterException(String message, Exception e) {
        super(message, e);
    }
}

package com.example.transfer.dbf.exception;

public class DbfException extends RuntimeException {

    public DbfException(String message) {
        super(message);
    }
    public DbfException(String message, Exception e) {
        super(message, e);
    }
}

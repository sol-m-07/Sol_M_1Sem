package com.example.todolist.exception;

public class BulkOperationException extends RuntimeException {

    public BulkOperationException(String message) {
        super(message);
    }
}

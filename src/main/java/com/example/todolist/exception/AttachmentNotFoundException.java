package com.example.todolist.exception;

public class AttachmentNotFoundException extends RuntimeException {

    public AttachmentNotFoundException(Long id) {
        super("Attachment not found with id: " + id);
    }
}

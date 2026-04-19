package com.example.todolist.dto;

public record TaskResponse(
        Long id,
        String title,
        boolean completed,
        boolean degraded,
        String message
) {
}

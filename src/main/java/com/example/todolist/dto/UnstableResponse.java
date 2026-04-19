package com.example.todolist.dto;

public record UnstableResponse(boolean degraded, String mode, String message) {
}

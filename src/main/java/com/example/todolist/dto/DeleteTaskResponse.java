package com.example.todolist.dto;

public record DeleteTaskResponse(boolean deleted, boolean degraded, String message) {
}

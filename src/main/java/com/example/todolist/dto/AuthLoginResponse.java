package com.example.todolist.dto;

public record AuthLoginResponse(String accessToken, String tokenType, long expiresInSeconds) {
}

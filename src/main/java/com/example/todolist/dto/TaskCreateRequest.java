package com.example.todolist.dto;

import jakarta.validation.constraints.NotBlank;

public record TaskCreateRequest(
        @NotBlank String title,
        boolean completed
) {
}

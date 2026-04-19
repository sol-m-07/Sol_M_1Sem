package com.example.todolist.dto;

import java.net.URI;

public record TaskCreateResult(TaskResponse task, URI location) {
}

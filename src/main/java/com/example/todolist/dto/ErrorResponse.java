package com.example.todolist.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Map;

@Schema(description = "Структура ответа об ошибке")
public class ErrorResponse {

    @Schema(description = "Время возникновения ошибки")
    private Instant timestamp;

    @Schema(description = "HTTP-статус", example = "400")
    private int status;

    @Schema(description = "Краткое описание ошибки", example = "Bad Request")
    private String error;

    @Schema(description = "Детальное сообщение", example = "Validation failed")
    private String message;

    @Schema(description = "Путь запроса", example = "/api/tasks")
    private String path;

    @Schema(description = "Дополнительные детали (ошибки полей и т.д.)")
    private Map<String, Object> details;

    public ErrorResponse() {
    }

    public ErrorResponse(Instant timestamp, int status, String error,
                         String message, String path, Map<String, Object> details) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.details = details;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }
}

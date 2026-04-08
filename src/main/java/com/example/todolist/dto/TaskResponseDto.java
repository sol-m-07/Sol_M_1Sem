package com.example.todolist.dto;

import com.example.todolist.model.Priority;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "DTO ответа задачи")
public class TaskResponseDto {

    @Schema(description = "ID задачи", example = "1")
    private Long id;

    @Schema(description = "Название задачи", example = "Buy groceries")
    private String title;

    @Schema(description = "Описание задачи", example = "Buy milk, bread, and eggs")
    private String description;

    @Schema(description = "Статус выполнения", example = "false")
    private boolean completed;

    @Schema(description = "Дата и время создания")
    private LocalDateTime createdAt;

    @Schema(description = "Дата и время обновления")
    private LocalDateTime updatedAt;

    @Schema(description = "Срок выполнения", example = "2026-12-31")
    private LocalDate dueDate;

    @Schema(description = "Приоритет задачи", example = "MEDIUM")
    private Priority priority;

    @Schema(description = "Набор тегов")
    private Set<String> tags;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
}

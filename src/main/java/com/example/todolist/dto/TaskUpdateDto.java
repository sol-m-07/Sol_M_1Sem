package com.example.todolist.dto;

import com.example.todolist.model.Priority;
import com.example.todolist.validation.DueDateNotBeforeCreation;
import com.example.todolist.validation.OnUpdate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@DueDateNotBeforeCreation(groups = OnUpdate.class)
@Schema(description = "DTO для обновления задачи")
public class TaskUpdateDto {

    @Size(min = 3, max = 100, groups = OnUpdate.class, message = "Title must be between 3 and 100 characters")
    @Schema(description = "Название задачи", example = "Buy groceries")
    private String title;

    @Size(max = 500, groups = OnUpdate.class, message = "Description must not exceed 500 characters")
    @Schema(description = "Описание задачи", example = "Updated description")
    private String description;

    @Schema(description = "Статус выполнения", example = "true")
    private Boolean completed;

    @FutureOrPresent(groups = OnUpdate.class, message = "Due date must not be in the past")
    @Schema(description = "Срок выполнения", example = "2026-12-31")
    private LocalDate dueDate;

    @Schema(description = "Приоритет задачи", example = "HIGH")
    private Priority priority;

    @Size(max = 5, groups = OnUpdate.class, message = "No more than 5 tags allowed")
    @Schema(description = "Набор тегов")
    private Set<String> tags;

    @Schema(hidden = true)
    private LocalDateTime createdAt;

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

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

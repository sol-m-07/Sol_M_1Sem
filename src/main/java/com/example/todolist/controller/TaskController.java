package com.example.todolist.controller;

import com.example.todolist.dto.TaskCreateDto;
import com.example.todolist.dto.TaskPriorityCountDto;
import com.example.todolist.dto.TaskResponseDto;
import com.example.todolist.dto.TaskUpdateDto;
import com.example.todolist.mapper.TaskMapper;
import com.example.todolist.model.Task;
import com.example.todolist.service.TaskService;
import com.example.todolist.service.TaskStatisticsJdbcService;
import com.example.todolist.validation.OnCreate;
import com.example.todolist.validation.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "CRUD-операции над задачами")
public class TaskController {

    private final TaskService taskService;
    private final TaskStatisticsJdbcService taskStatisticsJdbcService;
    private final TaskMapper taskMapper;

    @Value("${api.version}")
    private String apiVersion;

    public TaskController(TaskService taskService,
                          TaskStatisticsJdbcService taskStatisticsJdbcService,
                          TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskStatisticsJdbcService = taskStatisticsJdbcService;
        this.taskMapper = taskMapper;
    }

    @GetMapping
    @Operation(summary = "Получить все задачи", description = "Возвращает список всех задач")
    @ApiResponse(responseCode = "200", description = "Список задач получен")
    public ResponseEntity<List<TaskResponseDto>> getAllTasks() {
        List<Task> tasks = taskService.findAll();
        List<TaskResponseDto> dtos = tasks.stream()
                .map(taskMapper::toResponseDto)
                .toList();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(tasks.size()))
                .header("X-API-Version", apiVersion)
                .body(dtos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить задачу по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задача найдена"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long id) {
        Task task = taskService.findById(id);
        return ResponseEntity.ok()
                .header("X-API-Version", apiVersion)
                .body(taskMapper.toResponseDto(task));
    }

    @PostMapping
    @Operation(summary = "Создать задачу", description = "Создаёт новую задачу")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Задача создана"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные")
    })
    public ResponseEntity<TaskResponseDto> createTask(
            @Validated(OnCreate.class) @RequestBody TaskCreateDto dto) {
        Task task = taskMapper.toEntity(dto);
        Task saved = taskService.create(task);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("X-API-Version", apiVersion)
                .body(taskMapper.toResponseDto(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить задачу", description = "Обновляет существующую задачу по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задача обновлена"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable Long id,
            @Validated(OnUpdate.class) @RequestBody TaskUpdateDto dto) {
        Task existing = taskService.findById(id);
        dto.setCreatedAt(existing.getCreatedAt());
        taskMapper.updateEntity(dto, existing);
        Task updated = taskService.save(existing);
        return ResponseEntity.ok()
                .header("X-API-Version", apiVersion)
                .body(taskMapper.toResponseDto(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить задачу")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Задача удалена"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteById(id);
        return ResponseEntity.noContent()
                .header("X-API-Version", apiVersion)
                .build();
    }

    @PostMapping("/bulk-complete")
    @Operation(summary = "Пакетное завершение задач",
               description = "Помечает список задач как выполненные (транзакционно)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задачи обновлены"),
            @ApiResponse(responseCode = "400", description = "Одна из задач не найдена — откат")
    })
    public ResponseEntity<Void> bulkCompleteTasks(@RequestBody List<Long> ids) {
        taskService.bulkCompleteTasks(ids);
        return ResponseEntity.ok()
                .header("X-API-Version", apiVersion)
                .build();
    }

    @GetMapping("/with-attachments")
    @Operation(summary = "Получить задачи с вложениями",
               description = "Загружает задачи вместе с вложениями (решение N+1)")
    @ApiResponse(responseCode = "200", description = "Список задач с вложениями")
    public ResponseEntity<List<TaskResponseDto>> getAllTasksWithAttachments() {
        List<Task> tasks = taskService.findAllWithAttachments();
        List<TaskResponseDto> dtos = tasks.stream()
                .map(taskMapper::toResponseDto)
                .toList();
        return ResponseEntity.ok()
                .header("X-API-Version", apiVersion)
                .body(dtos);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Статистика по приоритетам",
               description = "Количество задач по приоритетам (JDBC)")
    @ApiResponse(responseCode = "200", description = "Статистика получена")
    public ResponseEntity<List<TaskPriorityCountDto>> getStatistics() {
        return ResponseEntity.ok()
                .header("X-API-Version", apiVersion)
                .body(taskStatisticsJdbcService.getTasksCountByPriority());
    }
}

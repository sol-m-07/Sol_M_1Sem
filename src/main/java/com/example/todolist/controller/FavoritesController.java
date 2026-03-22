package com.example.todolist.controller;

import com.example.todolist.dto.TaskResponseDto;
import com.example.todolist.mapper.TaskMapper;
import com.example.todolist.service.FavoritesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@Tag(name = "Favorites", description = "Управление избранными задачами (на основе сессии)")
public class FavoritesController {

    private final FavoritesService favoritesService;
    private final TaskMapper taskMapper;

    @Value("${api.version}")
    private String apiVersion;

    public FavoritesController(FavoritesService favoritesService, TaskMapper taskMapper) {
        this.favoritesService = favoritesService;
        this.taskMapper = taskMapper;
    }

    @PostMapping("/{taskId}")
    @Operation(summary = "Добавить в избранное")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задача добавлена в избранное"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    public ResponseEntity<Void> addFavorite(@PathVariable Long taskId, HttpSession session) {
        favoritesService.addFavorite(session, taskId);
        return ResponseEntity.ok()
                .header("X-API-Version", apiVersion)
                .build();
    }

    @DeleteMapping("/{taskId}")
    @Operation(summary = "Удалить из избранного")
    @ApiResponse(responseCode = "204", description = "Задача удалена из избранного")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long taskId, HttpSession session) {
        favoritesService.removeFavorite(session, taskId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .header("X-API-Version", apiVersion)
                .build();
    }

    @GetMapping
    @Operation(summary = "Получить избранные задачи", description = "Возвращает список избранных задач текущей сессии")
    @ApiResponse(responseCode = "200", description = "Список избранных задач")
    public ResponseEntity<List<TaskResponseDto>> getFavorites(HttpSession session) {
        List<TaskResponseDto> dtos = favoritesService.getFavorites(session).stream()
                .map(taskMapper::toResponseDto)
                .toList();
        return ResponseEntity.ok()
                .header("X-API-Version", apiVersion)
                .body(dtos);
    }
}

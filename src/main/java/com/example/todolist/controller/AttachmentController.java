package com.example.todolist.controller;

import com.example.todolist.dto.AttachmentResponseDto;
import com.example.todolist.mapper.AttachmentMapper;
import com.example.todolist.model.TaskAttachment;
import com.example.todolist.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Attachments", description = "Загрузка, скачивание и удаление файлов-вложений")
public class AttachmentController {

    private final AttachmentService attachmentService;
    private final AttachmentMapper attachmentMapper;

    @Value("${api.version}")
    private String apiVersion;

    public AttachmentController(AttachmentService attachmentService,
                                AttachmentMapper attachmentMapper) {
        this.attachmentService = attachmentService;
        this.attachmentMapper = attachmentMapper;
    }

    @PostMapping("/tasks/{taskId}/attachments")
    @Operation(summary = "Загрузить файл", description = "Загружает файл для указанной задачи")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Файл загружен"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    public ResponseEntity<AttachmentResponseDto> uploadAttachment(
            @PathVariable Long taskId,
            @RequestParam("file") MultipartFile file) throws IOException {
        TaskAttachment saved = attachmentService.storeAttachment(taskId, file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("X-API-Version", apiVersion)
                .body(attachmentMapper.toResponseDto(saved));
    }

    @GetMapping("/attachments/{attachmentId}")
    @Operation(summary = "Скачать файл", description = "Скачивает файл по ID вложения")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Файл скачан"),
            @ApiResponse(responseCode = "404", description = "Вложение не найдено")
    })
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long attachmentId) {
        TaskAttachment attachment = attachmentService.getAttachment(attachmentId);
        Resource resource = attachmentService.loadAsResource(attachmentId);
        return ResponseEntity.ok()
                .header("X-API-Version", apiVersion)
                .contentType(MediaType.parseMediaType(attachment.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + attachment.getFileName() + "\"")
                .body(resource);
    }

    @DeleteMapping("/attachments/{attachmentId}")
    @Operation(summary = "Удалить файл")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Файл удалён"),
            @ApiResponse(responseCode = "404", description = "Вложение не найдено")
    })
    public ResponseEntity<Void> deleteAttachment(@PathVariable Long attachmentId) {
        attachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.noContent()
                .header("X-API-Version", apiVersion)
                .build();
    }

    @GetMapping("/tasks/{taskId}/attachments")
    @Operation(summary = "Список вложений", description = "Получить все вложения задачи")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список вложений получен"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    public ResponseEntity<List<AttachmentResponseDto>> getAttachments(@PathVariable Long taskId) {
        List<AttachmentResponseDto> dtos = attachmentService.getAttachmentsByTaskId(taskId).stream()
                .map(attachmentMapper::toResponseDto)
                .toList();
        return ResponseEntity.ok()
                .header("X-API-Version", apiVersion)
                .body(dtos);
    }
}

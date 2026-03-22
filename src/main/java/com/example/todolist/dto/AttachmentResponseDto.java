package com.example.todolist.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "DTO ответа вложения")
public class AttachmentResponseDto {

    @Schema(description = "ID вложения", example = "1")
    private Long id;

    @Schema(description = "Имя файла", example = "report.pdf")
    private String fileName;

    @Schema(description = "Размер файла в байтах", example = "1024")
    private long size;

    @Schema(description = "Тип содержимого", example = "application/pdf")
    private String contentType;

    @Schema(description = "Дата и время загрузки")
    private LocalDateTime uploadedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}

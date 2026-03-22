package com.example.todolist.mapper;

import com.example.todolist.dto.AttachmentResponseDto;
import com.example.todolist.model.TaskAttachment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {

    AttachmentResponseDto toResponseDto(TaskAttachment attachment);
}

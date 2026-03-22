package com.example.todolist.repository;

import com.example.todolist.model.TaskAttachment;

import java.util.List;
import java.util.Optional;

public interface TaskAttachmentRepository {

    TaskAttachment save(TaskAttachment attachment);

    Optional<TaskAttachment> findById(Long id);

    List<TaskAttachment> findByTaskId(Long taskId);

    void deleteById(Long id);
}

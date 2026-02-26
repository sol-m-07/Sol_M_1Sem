package com.example.todolist.repository;

import com.example.todolist.model.Task;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для CRUD-операций над задачами.
 */
public interface TaskRepository {

    List<Task> findAll();

    Optional<Task> findById(Long id);

    Task save(Task task);

    void deleteById(Long id);

    boolean existsById(Long id);
}

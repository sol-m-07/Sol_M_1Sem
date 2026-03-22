package com.example.todolist.repository;

import com.example.todolist.model.Task;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Заглушка репозитория с фиксированными данными (для тестов и демонстрации @Qualifier).
 */
public class StubTaskRepository implements TaskRepository {

    private static final List<Task> STUB_TASKS = List.of(
            new Task(100L, "Stub Task 1", "Description from stub", false),
            new Task(101L, "Stub Task 2", "Another stub task", true)
    );

    @Override
    public List<Task> findAll() {
        return Collections.unmodifiableList(STUB_TASKS);
    }

    @Override
    public Optional<Task> findById(Long id) {
        return STUB_TASKS.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst();
    }

    @Override
    public Task save(Task task) {
        return task;
    }

    @Override
    public void deleteById(Long id) {
        // no-op for stub
    }

    @Override
    public boolean existsById(Long id) {
        return STUB_TASKS.stream().anyMatch(t -> t.getId().equals(id));
    }
}

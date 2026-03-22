package com.example.todolist.service;

import com.example.todolist.exception.TaskNotFoundException;
import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import com.example.todolist.repository.InMemoryTaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TaskServiceTest {

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(new InMemoryTaskRepository());
    }

    @Test
    @DisplayName("create sets createdAt and saves task")
    void create_setsCreatedAtAndSaves() {
        Task task = new Task(null, "Test", "Desc", false);
        task.setPriority(Priority.MEDIUM);

        Task saved = taskService.create(task);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Test");
    }

    @Test
    @DisplayName("findById returns task when exists")
    void findById_found() {
        Task task = taskService.create(new Task(null, "Find me", "Desc", false));
        Task found = taskService.findById(task.getId());
        assertThat(found.getTitle()).isEqualTo("Find me");
    }

    @Test
    @DisplayName("findById throws TaskNotFoundException when not found")
    void findById_notFound() {
        assertThatThrownBy(() -> taskService.findById(99999L))
                .isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    @DisplayName("deleteById removes task")
    void deleteById_removes() {
        Task task = taskService.create(new Task(null, "To delete", "Desc", false));
        taskService.deleteById(task.getId());
        assertThatThrownBy(() -> taskService.findById(task.getId()))
                .isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    @DisplayName("deleteById throws TaskNotFoundException for non-existing id")
    void deleteById_notFound() {
        assertThatThrownBy(() -> taskService.deleteById(99999L))
                .isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    @DisplayName("findAll returns all tasks")
    void findAll_returnsList() {
        taskService.create(new Task(null, "A", "DA", false));
        taskService.create(new Task(null, "B", "DB", false));
        List<Task> all = taskService.findAll();
        assertThat(all).hasSize(2);
    }
}

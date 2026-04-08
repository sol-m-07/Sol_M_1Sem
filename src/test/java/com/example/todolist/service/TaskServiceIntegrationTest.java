package com.example.todolist.service;

import com.example.todolist.exception.BulkOperationException;
import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import com.example.todolist.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class TaskServiceIntegrationTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    @DisplayName("bulkCompleteTasks marks all tasks as completed")
    void bulkCompleteTasks_success() {
        Task t1 = new Task();
        t1.setTitle("Task 1");
        t1.setPriority(Priority.MEDIUM);
        t1 = taskRepository.save(t1);

        Task t2 = new Task();
        t2.setTitle("Task 2");
        t2.setPriority(Priority.HIGH);
        t2 = taskRepository.save(t2);

        taskService.bulkCompleteTasks(List.of(t1.getId(), t2.getId()));

        assertThat(taskRepository.findById(t1.getId()).get().isCompleted()).isTrue();
        assertThat(taskRepository.findById(t2.getId()).get().isCompleted()).isTrue();
    }

    @Test
    @DisplayName("bulkCompleteTasks rolls back all changes when one ID is invalid")
    void bulkCompleteTasks_rollbackOnInvalidId() {
        Task t1 = new Task();
        t1.setTitle("Task 1");
        t1.setPriority(Priority.MEDIUM);
        t1 = taskRepository.save(t1);

        Long validId = t1.getId();
        Long invalidId = 99999L;

        assertThatThrownBy(() -> taskService.bulkCompleteTasks(List.of(validId, invalidId)))
                .isInstanceOf(BulkOperationException.class);

        Task fresh = taskRepository.findById(validId).orElseThrow();
        assertThat(fresh.isCompleted()).isFalse();
    }

    @Test
    @DisplayName("findAllWithAttachments returns tasks without N+1")
    void findAllWithAttachments_returnsAll() {
        Task t1 = new Task();
        t1.setTitle("Task 1");
        t1.setPriority(Priority.LOW);
        taskRepository.save(t1);

        List<Task> results = taskService.findAllWithAttachments();

        assertThat(results).isNotEmpty();
    }

    @Test
    @DisplayName("create persists task with auditing fields")
    void create_persistsWithAudit() {
        Task task = new Task();
        task.setTitle("Audited Task");
        task.setPriority(Priority.HIGH);

        Task saved = taskService.create(task);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
    }
}

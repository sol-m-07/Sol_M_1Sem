package com.example.todolist.service;

import com.example.todolist.exception.TaskNotFoundException;
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
class TaskServiceTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    @DisplayName("create saves task with createdAt from auditing")
    void create_setsCreatedAtAndSaves() {
        Task task = new Task();
        task.setTitle("Test");
        task.setDescription("Desc");
        task.setPriority(Priority.MEDIUM);

        Task saved = taskService.create(task);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Test");
    }

    @Test
    @DisplayName("findById returns task when exists")
    void findById_found() {
        Task task = new Task();
        task.setTitle("Find me");
        task.setDescription("Desc");
        task.setPriority(Priority.LOW);
        task = taskService.create(task);

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
        Task task = new Task();
        task.setTitle("To delete");
        task.setDescription("Desc");
        task.setPriority(Priority.MEDIUM);
        Task saved = taskService.create(task);

        taskService.deleteById(saved.getId());

        Long deletedId = saved.getId();
        assertThatThrownBy(() -> taskService.findById(deletedId))
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
        Task t1 = new Task();
        t1.setTitle("A");
        t1.setDescription("DA");
        t1.setPriority(Priority.LOW);
        taskService.create(t1);

        Task t2 = new Task();
        t2.setTitle("B");
        t2.setDescription("DB");
        t2.setPriority(Priority.HIGH);
        taskService.create(t2);

        List<Task> all = taskService.findAll();
        assertThat(all).hasSize(2);
    }
}

package com.example.todolist.service;

import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import com.example.todolist.repository.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class TaskServiceTest {

    @Autowired
    private TaskService taskService;

    @MockitoBean
    private TaskRepository taskRepository;

    @Test
    @DisplayName("save toggles completed flag of existing task and persists it via repository")
    void updateTaskStatus_existingTask_persistsUpdatedStatus() {
        Long taskId = 42L;
        Task existing = new Task();
        existing.setId(taskId);
        existing.setTitle("Write tests");
        existing.setPriority(Priority.HIGH);
        existing.setCompleted(false);

        given(taskRepository.findById(taskId)).willReturn(Optional.of(existing));
        given(taskRepository.save(any(Task.class))).willAnswer(inv -> inv.getArgument(0));

        Task loaded = taskService.findById(taskId);
        loaded.setCompleted(true);

        Task result = taskService.save(loaded);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(captor.capture());

        Task captured = captor.getValue();
        assertThat(captured.getId()).isEqualTo(taskId);
        assertThat(captured.isCompleted()).isTrue();
        assertThat(captured.getTitle()).isEqualTo("Write tests");

        assertThat(result.isCompleted()).isTrue();
    }
}

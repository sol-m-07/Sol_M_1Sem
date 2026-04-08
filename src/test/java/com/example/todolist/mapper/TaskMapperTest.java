package com.example.todolist.mapper;

import com.example.todolist.dto.TaskCreateDto;
import com.example.todolist.dto.TaskResponseDto;
import com.example.todolist.dto.TaskUpdateDto;
import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class TaskMapperTest {

    @Autowired
    private TaskMapper taskMapper;

    @Test
    @DisplayName("toEntity maps TaskCreateDto to Task correctly")
    void toEntity_mapsCorrectly() {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("New Task");
        dto.setDescription("Description");
        dto.setDueDate(LocalDate.of(2026, 12, 31));
        dto.setPriority(Priority.HIGH);
        dto.setTags(Set.of("urgent"));

        Task task = taskMapper.toEntity(dto);

        assertThat(task.getTitle()).isEqualTo("New Task");
        assertThat(task.getDescription()).isEqualTo("Description");
        assertThat(task.getDueDate()).isEqualTo(LocalDate.of(2026, 12, 31));
        assertThat(task.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(task.getTags()).containsExactly("urgent");
        assertThat(task.getId()).isNull();
    }

    @Test
    @DisplayName("toResponseDto maps Task to TaskResponseDto correctly")
    void toResponseDto_mapsCorrectly() {
        Task task = new Task(1L, "Task", "Desc", false);
        task.setCreatedAt(LocalDateTime.of(2026, 1, 1, 10, 0));
        task.setDueDate(LocalDate.of(2026, 6, 15));
        task.setPriority(Priority.LOW);
        task.setTags(Set.of("a", "b"));

        TaskResponseDto dto = taskMapper.toResponseDto(task);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getTitle()).isEqualTo("Task");
        assertThat(dto.getDescription()).isEqualTo("Desc");
        assertThat(dto.isCompleted()).isFalse();
        assertThat(dto.getCreatedAt()).isEqualTo(LocalDateTime.of(2026, 1, 1, 10, 0));
        assertThat(dto.getDueDate()).isEqualTo(LocalDate.of(2026, 6, 15));
        assertThat(dto.getPriority()).isEqualTo(Priority.LOW);
        assertThat(dto.getTags()).containsExactlyInAnyOrder("a", "b");
    }

    @Test
    @DisplayName("updateEntity only updates non-null fields")
    void updateEntity_partialUpdate() {
        Task task = new Task(1L, "Original", "Original Desc", false);
        task.setPriority(Priority.MEDIUM);
        task.setTags(Set.of("old"));

        TaskUpdateDto dto = new TaskUpdateDto();
        dto.setTitle("Updated");
        dto.setCompleted(true);

        taskMapper.updateEntity(dto, task);

        assertThat(task.getTitle()).isEqualTo("Updated");
        assertThat(task.isCompleted()).isTrue();
        assertThat(task.getDescription()).isEqualTo("Original Desc");
        assertThat(task.getPriority()).isEqualTo(Priority.MEDIUM);
        assertThat(task.getTags()).containsExactly("old");
    }
}

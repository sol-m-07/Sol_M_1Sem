package com.example.todolist.controller;

import com.example.todolist.dto.TaskCreateDto;
import com.example.todolist.mapper.TaskMapperImpl;
import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import com.example.todolist.service.TaskService;
import com.example.todolist.service.TaskStatisticsJdbcService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@Import(TaskMapperImpl.class)
@TestPropertySource(properties = "api.version=2.0.0")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private TaskStatisticsJdbcService taskStatisticsJdbcService;

    @Test
    @DisplayName("POST /api/tasks — valid DTO returns 201 and serialized JSON body")
    void createTask_returns201_andJsonBody() throws Exception {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Buy groceries");
        dto.setDescription("Milk, bread, eggs");
        dto.setDueDate(LocalDate.now().plusDays(3));
        dto.setPriority(Priority.HIGH);
        dto.setTags(Set.of("home", "shopping"));

        Task saved = new Task();
        saved.setId(1L);
        saved.setTitle("Buy groceries");
        saved.setDescription("Milk, bread, eggs");
        saved.setCompleted(false);
        saved.setCreatedAt(LocalDateTime.of(2026, 1, 1, 12, 0));
        saved.setDueDate(dto.getDueDate());
        saved.setPriority(Priority.HIGH);
        saved.setTags(dto.getTags());
        given(taskService.create(any(Task.class))).willReturn(saved);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Buy groceries"))
                .andExpect(jsonPath("$.description").value("Milk, bread, eggs"))
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.tags", org.hamcrest.Matchers.containsInAnyOrder("home", "shopping")));

        verify(taskService).create(any(Task.class));
    }

    @Test
    @DisplayName("POST /api/tasks — blank title triggers validation and returns 400")
    void createTask_blankTitle_returns400() throws Exception {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("");
        dto.setPriority(Priority.MEDIUM);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details.title").exists());
    }

    @Test
    @DisplayName("GET /api/tasks/{id} — existing task returns 200 and matching JSON body")
    void getTaskById_returns200_andJsonBody() throws Exception {
        Long taskId = 7L;
        Task stored = new Task();
        stored.setId(taskId);
        stored.setTitle("Read book");
        stored.setDescription("Clean Architecture");
        stored.setCompleted(true);
        stored.setCreatedAt(LocalDateTime.of(2026, 4, 10, 9, 30));
        stored.setUpdatedAt(LocalDateTime.of(2026, 4, 11, 10, 0));
        stored.setDueDate(LocalDate.of(2026, 5, 1));
        stored.setPriority(Priority.LOW);
        stored.setTags(Set.of("reading"));
        given(taskService.findById(taskId)).willReturn(stored);

        mockMvc.perform(get("/api/tasks/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.title").value("Read book"))
                .andExpect(jsonPath("$.description").value("Clean Architecture"))
                .andExpect(jsonPath("$.completed").value(true))
                .andExpect(jsonPath("$.priority").value("LOW"))
                .andExpect(jsonPath("$.dueDate").value("2026-05-01"))
                .andExpect(jsonPath("$.tags[0]").value("reading"));

        verify(taskService).findById(eq(taskId));
    }
}

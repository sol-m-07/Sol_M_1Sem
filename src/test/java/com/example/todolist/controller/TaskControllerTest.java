package com.example.todolist.controller;

import com.example.todolist.dto.TaskCreateDto;
import com.example.todolist.dto.TaskResponseDto;
import com.example.todolist.model.Priority;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private TaskCreateDto validCreateDto() {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Test Task");
        dto.setDescription("A test description");
        dto.setDueDate(LocalDate.now().plusDays(7));
        dto.setPriority(Priority.MEDIUM);
        dto.setTags(Set.of("test", "unit"));
        return dto;
    }

    private Long createTaskAndGetId() throws Exception {
        TaskCreateDto dto = validCreateDto();
        MvcResult result = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();
        TaskResponseDto response = objectMapper.readValue(
                result.getResponse().getContentAsString(), TaskResponseDto.class);
        return response.getId();
    }

    @Test
    @DisplayName("GET /api/tasks — returns list with X-Total-Count header")
    void getAllTasks_positive() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Total-Count"))
                .andExpect(header().string("X-API-Version", "2.0.0"))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/tasks/{id} — existing task returns 200")
    void getTaskById_positive() throws Exception {
        Long id = createTaskAndGetId();
        mockMvc.perform(get("/api/tasks/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.priority").value("MEDIUM"))
                .andExpect(header().string("X-API-Version", "2.0.0"));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} — non-existing returns 404")
    void getTaskById_negative_notFound() throws Exception {
        mockMvc.perform(get("/api/tasks/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("POST /api/tasks — valid DTO returns 201")
    void createTask_positive() throws Exception {
        TaskCreateDto dto = validCreateDto();
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.priority").value("MEDIUM"));
    }

    @Test
    @DisplayName("POST /api/tasks — blank title returns 400")
    void createTask_negative_blankTitle() throws Exception {
        TaskCreateDto dto = validCreateDto();
        dto.setTitle("");
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.title").exists());
    }

    @Test
    @DisplayName("POST /api/tasks — short title returns 400")
    void createTask_negative_shortTitle() throws Exception {
        TaskCreateDto dto = validCreateDto();
        dto.setTitle("ab");
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.title").exists());
    }

    @Test
    @DisplayName("POST /api/tasks — null priority returns 400")
    void createTask_negative_nullPriority() throws Exception {
        TaskCreateDto dto = validCreateDto();
        dto.setPriority(null);
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.priority").exists());
    }

    @Test
    @DisplayName("POST /api/tasks — past dueDate returns 400")
    void createTask_negative_pastDueDate() throws Exception {
        TaskCreateDto dto = validCreateDto();
        dto.setDueDate(LocalDate.of(2020, 1, 1));
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.dueDate").exists());
    }

    @Test
    @DisplayName("POST /api/tasks — too many tags returns 400")
    void createTask_negative_tooManyTags() throws Exception {
        TaskCreateDto dto = validCreateDto();
        dto.setTags(Set.of("a", "b", "c", "d", "e", "f"));
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.tags").exists());
    }

    @Test
    @DisplayName("POST /api/tasks — malformed JSON returns 400")
    void createTask_negative_malformedJson() throws Exception {
        String malformedJson = "not a valid json";
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} — partial update returns 200")
    void updateTask_positive() throws Exception {
        Long id = createTaskAndGetId();
        String updateJson = "{\"title\": \"Updated Title\", \"completed\": true}";
        mockMvc.perform(put("/api/tasks/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.completed").value(true))
                .andExpect(jsonPath("$.priority").value("MEDIUM"));
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} — non-existing returns 404")
    void updateTask_negative_notFound() throws Exception {
        String updateJson = "{\"title\": \"Updated\"}";
        mockMvc.perform(put("/api/tasks/99999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} — title too short returns 400")
    void updateTask_negative_shortTitle() throws Exception {
        Long id = createTaskAndGetId();
        String updateJson = "{\"title\": \"ab\"}";
        mockMvc.perform(put("/api/tasks/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.title").exists());
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} — existing task returns 204")
    void deleteTask_positive() throws Exception {
        Long id = createTaskAndGetId();
        mockMvc.perform(delete("/api/tasks/" + id))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/tasks/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} — non-existing returns 404")
    void deleteTask_negative_notFound() throws Exception {
        mockMvc.perform(delete("/api/tasks/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/tasks/statistics — returns priority statistics via JDBC")
    void getStatistics_positive() throws Exception {
        mockMvc.perform(get("/api/tasks/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(header().string("X-API-Version", "2.0.0"));
    }
}

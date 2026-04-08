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
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FavoritesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long createTaskAndGetId() throws Exception {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Fav Task");
        dto.setDescription("Test");
        dto.setDueDate(LocalDate.now().plusDays(7));
        dto.setPriority(Priority.HIGH);
        dto.setTags(Set.of("fav"));
        MvcResult result = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readValue(
                result.getResponse().getContentAsString(), TaskResponseDto.class).getId();
    }

    @Test
    @DisplayName("POST /api/favorites/{taskId} — add to favorites returns 200")
    void addFavorite_positive() throws Exception {
        Long taskId = createTaskAndGetId();
        mockMvc.perform(post("/api/favorites/" + taskId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/favorites/{taskId} — non-existing task returns 404")
    void addFavorite_negative_notFound() throws Exception {
        mockMvc.perform(post("/api/favorites/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/favorites — returns favorites within same session")
    void getFavorites_positive() throws Exception {
        Long taskId = createTaskAndGetId();
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/api/favorites/" + taskId).session(session))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/favorites").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(taskId));
    }

    @Test
    @DisplayName("DELETE /api/favorites/{taskId} — remove from favorites returns 204")
    void removeFavorite_positive() throws Exception {
        Long taskId = createTaskAndGetId();
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/api/favorites/" + taskId).session(session))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/favorites/" + taskId).session(session))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/favorites").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("GET /api/favorites — empty without session data")
    void getFavorites_empty() throws Exception {
        mockMvc.perform(get("/api/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}

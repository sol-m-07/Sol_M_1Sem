package com.example.todolist.controller;

import com.example.todolist.dto.AttachmentResponseDto;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AttachmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long createTaskAndGetId() throws Exception {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Task for attachment");
        dto.setDescription("Test");
        dto.setDueDate(LocalDate.now().plusDays(7));
        dto.setPriority(Priority.MEDIUM);
        dto.setTags(Set.of("test"));
        MvcResult result = mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .post("/api/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readValue(
                result.getResponse().getContentAsString(), TaskResponseDto.class).getId();
    }

    @Test
    @DisplayName("POST /api/tasks/{taskId}/attachments — upload file returns 201")
    void uploadAttachment_positive() throws Exception {
        Long taskId = createTaskAndGetId();
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "Hello World".getBytes());
        mockMvc.perform(multipart("/api/tasks/" + taskId + "/attachments").file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.fileName").value("test.txt"))
                .andExpect(jsonPath("$.size").value(11));
    }

    @Test
    @DisplayName("POST /api/tasks/{taskId}/attachments — non-existing task returns 404")
    void uploadAttachment_negative_taskNotFound() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "data".getBytes());
        mockMvc.perform(multipart("/api/tasks/99999/attachments").file(file))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/attachments/{id} — download returns file content")
    void downloadAttachment_positive() throws Exception {
        Long taskId = createTaskAndGetId();
        MockMultipartFile file = new MockMultipartFile(
                "file", "download.txt", "text/plain", "content".getBytes());
        MvcResult uploadResult = mockMvc.perform(
                        multipart("/api/tasks/" + taskId + "/attachments").file(file))
                .andExpect(status().isCreated())
                .andReturn();
        AttachmentResponseDto att = objectMapper.readValue(
                uploadResult.getResponse().getContentAsString(), AttachmentResponseDto.class);

        mockMvc.perform(get("/api/attachments/" + att.getId()))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        "attachment; filename=\"download.txt\""));
    }

    @Test
    @DisplayName("GET /api/attachments/{id} — non-existing returns 404")
    void downloadAttachment_negative_notFound() throws Exception {
        mockMvc.perform(get("/api/attachments/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/attachments/{id} — delete returns 204")
    void deleteAttachment_positive() throws Exception {
        Long taskId = createTaskAndGetId();
        MockMultipartFile file = new MockMultipartFile(
                "file", "todelete.txt", "text/plain", "data".getBytes());
        MvcResult uploadResult = mockMvc.perform(
                        multipart("/api/tasks/" + taskId + "/attachments").file(file))
                .andExpect(status().isCreated())
                .andReturn();
        AttachmentResponseDto att = objectMapper.readValue(
                uploadResult.getResponse().getContentAsString(), AttachmentResponseDto.class);

        mockMvc.perform(delete("/api/attachments/" + att.getId()))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/attachments/" + att.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/tasks/{taskId}/attachments — list attachments")
    void getAttachments_positive() throws Exception {
        Long taskId = createTaskAndGetId();
        MockMultipartFile file = new MockMultipartFile(
                "file", "list.txt", "text/plain", "data".getBytes());
        mockMvc.perform(multipart("/api/tasks/" + taskId + "/attachments").file(file))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/tasks/" + taskId + "/attachments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].fileName").value("list.txt"));
    }

    @Test
    @DisplayName("GET /api/tasks/{taskId}/attachments — non-existing task returns 404")
    void getAttachments_negative_taskNotFound() throws Exception {
        mockMvc.perform(get("/api/tasks/99999/attachments"))
                .andExpect(status().isNotFound());
    }
}

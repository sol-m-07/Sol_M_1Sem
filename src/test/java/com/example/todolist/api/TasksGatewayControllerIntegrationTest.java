package com.example.todolist.api;

import com.example.todolist.client.ExternalTasksClient;
import com.example.todolist.dto.TaskCreateRequest;
import com.example.todolist.dto.TaskCreateResult;
import com.example.todolist.dto.TaskResponse;
import com.example.todolist.exception.TaskNotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.net.URI;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "resilience4j.ratelimiter.instances.externalApi.limitForPeriod=1000",
        "resilience4j.ratelimiter.instances.externalApi.limitRefreshPeriod=1s",
        "resilience4j.ratelimiter.instances.externalApi.timeoutDuration=0"
})
@AutoConfigureMockMvc
class TasksGatewayControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ExternalTasksClient externalTasksClient;

    @Test
    void createTaskReturnsCreatedAndLocation() throws Exception {
        when(externalTasksClient.createTask(any(TaskCreateRequest.class)))
                .thenReturn(new TaskCreateResult(
                        new TaskResponse(101L, "demo", false, false, null),
                        URI.create("/external/v1/tasks/101")
                ));

        String token = loginAndExtractToken("user", "password");

        mockMvc.perform(post("/api/v1/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"demo","completed":false}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/external/v1/tasks/101"));
    }

    @Test
    void getMissingTaskReturnsNotFound() throws Exception {
        when(externalTasksClient.getTask(999999L)).thenThrow(new TaskNotFoundException("Task 999999 not found"));
        String token = loginAndExtractToken("user", "password");

        mockMvc.perform(get("/api/v1/tasks/999999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void listTasksPassesQueryParamsToClient() throws Exception {
        when(externalTasksClient.listTasks(false, 10))
                .thenReturn(List.of(new TaskResponse(1L, "t1", false, false, null)));
        String token = loginAndExtractToken("user", "password");

        mockMvc.perform(get("/api/v1/tasks")
                        .queryParam("completed", "false")
                        .queryParam("limit", "10")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        verify(externalTasksClient).listTasks(eq(false), eq(10));
    }

    @Test
    void deleteTaskReturnsNoContent() throws Exception {
        doNothing().when(externalTasksClient).deleteTask(42L);
        String token = loginAndExtractToken("user", "password");

        mockMvc.perform(delete("/api/v1/tasks/42")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    private String loginAndExtractToken(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"%s","password":"%s"}
                                """.formatted(username, password)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("accessToken").asText();
    }
}

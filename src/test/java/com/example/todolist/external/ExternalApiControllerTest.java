package com.example.todolist.external;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ExternalApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createReturns201WithLocation() throws Exception {
        mockMvc.perform(post("/external/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"external-demo","completed":false}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void deleteMissingReturns404ProblemDetails() throws Exception {
        mockMvc.perform(delete("/external/v1/tasks/999999"))
                .andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", org.hamcrest.Matchers.containsString("application/problem+json")));
    }

    @Test
    void unstable429ReturnsRetryAfterHeader() throws Exception {
        mockMvc.perform(get("/external/v1/unstable")
                        .queryParam("mode", "429"))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().string("Retry-After", "2"));
    }

    @Test
    void unstableHtmlReturns502WithHtmlType() throws Exception {
        mockMvc.perform(get("/external/v1/unstable")
                        .queryParam("mode", "html"))
                .andExpect(status().isBadGateway())
                .andExpect(header().string("Content-Type", org.hamcrest.Matchers.containsString("text/html")));
    }

    @Test
    void deleteExistingReturns204() throws Exception {
        MvcResult created = mockMvc.perform(post("/external/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"to-delete","completed":false}
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        String location = created.getResponse().getHeader("Location");
        mockMvc.perform(delete(location))
                .andExpect(status().isNoContent());
    }
}

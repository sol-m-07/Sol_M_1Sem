package com.example.todolist.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.servlet.http.Cookie;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PreferencesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/preferences/view — default value returned when no cookie")
    void getViewPreference_default() throws Exception {
        mockMvc.perform(get("/api/preferences/view"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.viewPreference").value("detailed"));
    }

    @Test
    @DisplayName("GET /api/preferences/view — reads existing cookie")
    void getViewPreference_withCookie() throws Exception {
        mockMvc.perform(get("/api/preferences/view")
                        .cookie(new Cookie("viewPreference", "compact")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.viewPreference").value("compact"));
    }

    @Test
    @DisplayName("POST /api/preferences/view?mode=compact — sets cookie")
    void setViewPreference_compact() throws Exception {
        mockMvc.perform(post("/api/preferences/view").param("mode", "compact"))
                .andExpect(status().isOk())
                .andExpect(cookie().value("viewPreference", "compact"))
                .andExpect(jsonPath("$.viewPreference").value("compact"));
    }

    @Test
    @DisplayName("POST /api/preferences/view?mode=detailed — sets cookie")
    void setViewPreference_detailed() throws Exception {
        mockMvc.perform(post("/api/preferences/view").param("mode", "detailed"))
                .andExpect(status().isOk())
                .andExpect(cookie().value("viewPreference", "detailed"))
                .andExpect(jsonPath("$.viewPreference").value("detailed"));
    }

    @Test
    @DisplayName("POST /api/preferences/view?mode=invalid — defaults to detailed")
    void setViewPreference_invalid_defaultsToDetailed() throws Exception {
        mockMvc.perform(post("/api/preferences/view").param("mode", "invalid"))
                .andExpect(status().isOk())
                .andExpect(cookie().value("viewPreference", "detailed"))
                .andExpect(jsonPath("$.viewPreference").value("detailed"));
    }
}

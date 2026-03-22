package com.example.todolist.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/preferences")
@Tag(name = "Preferences", description = "Пользовательские настройки (куки)")
public class PreferencesController {

    private static final String COOKIE_NAME = "viewPreference";
    private static final String DEFAULT_MODE = "detailed";
    private static final int COOKIE_MAX_AGE = 30 * 24 * 60 * 60; // 30 days

    @Value("${api.version}")
    private String apiVersion;

    @GetMapping("/view")
    @Operation(summary = "Получить настройку отображения",
               description = "Читает куку viewPreference")
    @ApiResponse(responseCode = "200", description = "Настройка получена")
    public ResponseEntity<Map<String, String>> getViewPreference(
            @CookieValue(name = COOKIE_NAME, defaultValue = DEFAULT_MODE) String viewPreference) {
        return ResponseEntity.ok()
                .header("X-API-Version", apiVersion)
                .body(Map.of("viewPreference", viewPreference));
    }

    @PostMapping("/view")
    @Operation(summary = "Установить настройку отображения",
               description = "Устанавливает куку viewPreference: compact или detailed")
    @ApiResponse(responseCode = "200", description = "Настройка обновлена")
    public ResponseEntity<Map<String, String>> setViewPreference(
            @RequestParam String mode,
            HttpServletResponse response) {
        String value = ("compact".equals(mode) || "detailed".equals(mode)) ? mode : DEFAULT_MODE;
        Cookie cookie = new Cookie(COOKIE_NAME, value);
        cookie.setPath("/");
        cookie.setMaxAge(COOKIE_MAX_AGE);
        cookie.setHttpOnly(false);
        response.addCookie(cookie);
        return ResponseEntity.ok()
                .header("X-API-Version", apiVersion)
                .body(Map.of("viewPreference", value));
    }
}

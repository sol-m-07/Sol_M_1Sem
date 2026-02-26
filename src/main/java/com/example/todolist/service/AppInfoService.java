package com.example.todolist.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.TreeMap;

/**
 * Сервис с примером инжекции кастомных свойств через @Value (app.name, app.version).
 */
@Service
public class AppInfoService {

    @Value("${app.name}")
    private String appName;

    @Value("${app.version}")
    private String appVersion;

    /**
     * Возвращает информацию о приложении из конфигурации (демонстрация @Value).
     */
    public Map<String, String> getAppInfo() {
        Map<String, String> info = new TreeMap<>();
        info.put("name", appName);
        info.put("version", appVersion);
        return info;
    }
}

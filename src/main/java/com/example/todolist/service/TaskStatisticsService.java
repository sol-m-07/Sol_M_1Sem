package com.example.todolist.service;

import com.example.todolist.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Сервис статистики, демонстрирующий работу @Qualifier:
 * инжектирует основной репозиторий (через @Primary) и заглушку (через @Qualifier).
 */
@Service
public class TaskStatisticsService {

    private final TaskRepository primaryRepository;
    private final TaskRepository stubTaskRepository;

    public TaskStatisticsService(
            TaskRepository primaryRepository,
            @Qualifier("stubTaskRepository") TaskRepository stubTaskRepository) {
        this.primaryRepository = primaryRepository;
        this.stubTaskRepository = stubTaskRepository;
    }

    /**
     * Сравнивает количество задач в основном репозитории и в заглушке.
     */
    public Map<String, Object> getRepositoryComparison() {
        int primaryCount = primaryRepository.findAll().size();
        int stubCount = stubTaskRepository.findAll().size();
        Map<String, Object> result = new HashMap<>();
        result.put("primaryRepository", "InMemoryTaskRepository");
        result.put("primaryTaskCount", primaryCount);
        result.put("stubRepository", "StubTaskRepository");
        result.put("stubTaskCount", stubCount);
        result.put("difference", primaryCount - stubCount);
        return result;
    }
}

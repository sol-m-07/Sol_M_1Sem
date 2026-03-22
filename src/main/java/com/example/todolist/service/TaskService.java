package com.example.todolist.service;

import com.example.todolist.exception.TaskNotFoundException;
import com.example.todolist.model.Task;
import com.example.todolist.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;

    private final Map<String, Task> taskCache = new ConcurrentHashMap<>();

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @PostConstruct
    public void initCache() {
        List<Task> tasks = taskRepository.findAll();
        for (Task task : tasks) {
            if (task.getId() != null) {
                taskCache.put(task.getId().toString(), task);
            }
        }
        log.info("[TaskService] Cache initialized with {} tasks from repository", taskCache.size());
    }

    @PreDestroy
    public void cleanup() {
        int count = taskCache.size();
        log.info("[TaskService] PreDestroy: clearing cache, current task count = {}", count);
        try {
            Path statsFile = Path.of("task-service-stats.txt");
            String content = String.format("TaskService shutdown stats%ntasks in cache: %d%n", count);
            Files.writeString(statsFile, content);
            log.info("[TaskService] Statistics saved to {}", statsFile.toAbsolutePath());
        } catch (IOException e) {
            log.warn("[TaskService] Failed to save statistics to file", e);
        }
        taskCache.clear();
    }

    public Map<String, Task> getTaskCache() {
        return taskCache;
    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    public Task create(Task task) {
        task.setCreatedAt(LocalDateTime.now());
        Task saved = taskRepository.save(task);
        if (saved.getId() != null) {
            taskCache.put(saved.getId().toString(), saved);
        }
        return saved;
    }

    public Task save(Task task) {
        Task saved = taskRepository.save(task);
        if (saved.getId() != null) {
            taskCache.put(saved.getId().toString(), saved);
        }
        return saved;
    }

    public void deleteById(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        taskRepository.deleteById(id);
        taskCache.remove(id.toString());
    }

    public boolean existsById(Long id) {
        return taskRepository.existsById(id);
    }
}

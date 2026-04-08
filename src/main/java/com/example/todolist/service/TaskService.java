package com.example.todolist.service;

import com.example.todolist.exception.BulkOperationException;
import com.example.todolist.exception.TaskNotFoundException;
import com.example.todolist.model.Task;
import com.example.todolist.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @PostConstruct
    public void init() {
        log.info("[TaskService] initialized");
    }

    @PreDestroy
    public void cleanup() {
        log.info("[TaskService] shutting down");
    }

    @Transactional(readOnly = true)
    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @Transactional
    public Task create(Task task) {
        return taskRepository.save(task);
    }

    @Transactional
    public Task save(Task task) {
        return taskRepository.save(task);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        taskRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return taskRepository.existsById(id);
    }

    @Transactional(
            readOnly = false,
            isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED,
            rollbackFor = BulkOperationException.class
    )
    public void bulkCompleteTasks(List<Long> ids) {
        List<Task> tasks = new ArrayList<>();
        for (Long id : ids) {
            Task task = taskRepository.findById(id)
                    .orElseThrow(() -> new BulkOperationException(
                            "Task not found with id: " + id));
            task.setCompleted(true);
            tasks.add(task);
        }
        taskRepository.saveAll(tasks);
    }

    @Transactional(readOnly = true)
    public List<Task> findAllWithAttachments() {
        return taskRepository.findAllWithAttachments();
    }

    @Transactional(readOnly = true)
    public List<Task> findTasksDueSoon() {
        return taskRepository.findTasksDueSoon(LocalDate.now().plusDays(7));
    }
}

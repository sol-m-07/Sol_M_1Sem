package com.example.todolist.service;

import com.example.todolist.client.ExternalTasksClient;
import com.example.todolist.dto.DeleteTaskResponse;
import com.example.todolist.dto.TaskCreateRequest;
import com.example.todolist.dto.TaskCreateResult;
import com.example.todolist.dto.TaskResponse;
import com.example.todolist.dto.UnstableResponse;
import com.example.todolist.exception.TaskNotFoundException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.List;

@Service
public class TasksGatewayService {

    private final ExternalTasksClient externalTasksClient;

    public TasksGatewayService(ExternalTasksClient externalTasksClient) {
        this.externalTasksClient = externalTasksClient;
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "createTaskFallback")
    public TaskCreateResult createTask(TaskCreateRequest request) {
        return externalTasksClient.createTask(request);
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "getTaskFallback")
    public TaskResponse getTask(Long id) {
        return externalTasksClient.getTask(id);
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "listTasksFallback")
    public List<TaskResponse> listTasks(Boolean completed, Integer limit) {
        return externalTasksClient.listTasks(completed, limit);
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "deleteTaskFallback")
    public DeleteTaskResponse deleteTask(Long id) {
        externalTasksClient.deleteTask(id);
        return new DeleteTaskResponse(true, false, "Task deleted");
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "callUnstableFallback")
    public UnstableResponse callUnstable(String mode) {
        String result = externalTasksClient.callUnstable(mode);
        return new UnstableResponse(false, mode, result);
    }

    private TaskCreateResult createTaskFallback(TaskCreateRequest request, Throwable throwable) {
        throwIfNonFallbackable(throwable);
        TaskResponse fallbackTask = new TaskResponse(
                -1L,
                request.title(),
                request.completed(),
                true,
                "External API unavailable, returning fallback task"
        );
        return new TaskCreateResult(fallbackTask, URI.create("/api/v1/tasks/fallback"));
    }

    private TaskResponse getTaskFallback(Long id, Throwable throwable) {
        throwIfNonFallbackable(throwable);
        return new TaskResponse(
                id,
                "Fallback task",
                false,
                true,
                "Task data temporarily unavailable: " + throwable.getClass().getSimpleName()
        );
    }

    private List<TaskResponse> listTasksFallback(Boolean completed, Integer limit, Throwable throwable) {
        throwIfNonFallbackable(throwable);
        return Collections.singletonList(new TaskResponse(
                -1L,
                "Fallback list item",
                false,
                true,
                "External API unavailable, fallback list returned"
        ));
    }

    private DeleteTaskResponse deleteTaskFallback(Long id, Throwable throwable) {
        throwIfNonFallbackable(throwable);
        return new DeleteTaskResponse(
                false,
                true,
                "Delete fallback: external API unavailable"
        );
    }

    private UnstableResponse callUnstableFallback(String mode, Throwable throwable) {
        throwIfNonFallbackable(throwable);
        return new UnstableResponse(
                true,
                mode,
                "Fallback response because external unstable endpoint failed: " + throwable.getClass().getSimpleName()
        );
    }

    private void throwIfNonFallbackable(Throwable throwable) {
        Throwable root = unwrap(throwable);
        if (root instanceof TaskNotFoundException taskNotFoundException) {
            throw taskNotFoundException;
        }
        if (root instanceof RequestNotPermitted requestNotPermitted) {
            throw requestNotPermitted;
        }
    }

    private Throwable unwrap(Throwable throwable) {
        Throwable current = throwable;
        ArrayDeque<Throwable> visited = new ArrayDeque<>();
        while (current != null && current.getCause() != null && current.getCause() != current) {
            if (visited.contains(current)) {
                break;
            }
            visited.add(current);
            current = current.getCause();
        }
        return current == null ? throwable : current;
    }
}

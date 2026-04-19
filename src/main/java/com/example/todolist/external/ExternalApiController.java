package com.example.todolist.external;

import com.example.todolist.dto.TaskCreateRequest;
import com.example.todolist.dto.TaskResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/external/v1")
public class ExternalApiController {

    private final Map<Long, TaskResponse> tasks = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(1000L);

    @PostMapping("/tasks")
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskCreateRequest request) {
        long id = idSequence.incrementAndGet();
        TaskResponse task = new TaskResponse(id, request.title(), request.completed(), false, null);
        tasks.put(id, task);
        URI location = URI.create("/external/v1/tasks/" + id);
        return ResponseEntity.created(location).body(task);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<?> getTask(@PathVariable Long id) {
        TaskResponse task = tasks.get(id);
        if (task == null) {
            return notFoundProblem(id);
        }
        return ResponseEntity.ok(task);
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskResponse>> listTasks(@RequestParam(required = false) Boolean completed,
                                                        @RequestParam(required = false) Integer limit) {
        List<TaskResponse> list = new ArrayList<>(tasks.values())
                .stream()
                .sorted(Comparator.comparing(TaskResponse::id))
                .toList();
        if (completed != null) {
            list = list.stream().filter(item -> item.completed() == completed).toList();
        }
        if (limit != null && limit >= 0 && limit < list.size()) {
            list = list.subList(0, limit);
        }
        return ResponseEntity.ok(list);
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @Valid @RequestBody TaskCreateRequest request) {
        if (!tasks.containsKey(id)) {
            return notFoundProblem(id);
        }
        TaskResponse updated = new TaskResponse(id, request.title(), request.completed(), false, null);
        tasks.put(id, updated);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        TaskResponse removed = tasks.remove(id);
        if (removed == null) {
            return notFoundProblem(id);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unstable")
    public ResponseEntity<?> unstable(@RequestParam String mode) throws InterruptedException {
        switch (mode) {
            case "timeout" -> {
                Thread.sleep(Duration.ofSeconds(5).toMillis());
                return ResponseEntity.ok(Map.of("status", "late"));
            }
            case "500" -> {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Simulated internal error"));
            }
            case "429" -> {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .header(HttpHeaders.RETRY_AFTER, "2")
                        .body(ProblemDetail.forStatusAndDetail(HttpStatus.TOO_MANY_REQUESTS, "Too many requests"));
            }
            case "html" -> {
                String html = "<html><body><h1>Upstream Bad Gateway</h1></body></html>";
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .contentType(MediaType.TEXT_HTML)
                        .body(html);
            }
            default -> {
                return ResponseEntity.badRequest()
                        .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Unknown mode"));
            }
        }
    }

    private ResponseEntity<ProblemDetail> notFoundProblem(Long id) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Task " + id + " not found");
        detail.setTitle("Task not found");
        detail.setProperty("taskId", id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(detail);
    }
}

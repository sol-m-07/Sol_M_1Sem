package com.example.todolist.api;

import com.example.todolist.dto.DeleteTaskResponse;
import com.example.todolist.dto.TaskCreateRequest;
import com.example.todolist.dto.TaskCreateResult;
import com.example.todolist.dto.TaskResponse;
import com.example.todolist.dto.UnstableResponse;
import com.example.todolist.service.TasksGatewayService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TasksGatewayController {

    private final TasksGatewayService tasksGatewayService;

    public TasksGatewayController(TasksGatewayService tasksGatewayService) {
        this.tasksGatewayService = tasksGatewayService;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskCreateRequest request) {
        TaskCreateResult result = tasksGatewayService.createTask(request);
        URI location = result.location() != null ? result.location() : URI.create("/api/v1/tasks/" + result.task().id());
        return ResponseEntity.created(location).body(result.task());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable Long id) {
        return ResponseEntity.ok(tasksGatewayService.getTask(id));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> listTasks(@RequestParam(required = false) Boolean completed,
                                                        @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(tasksGatewayService.listTasks(completed, limit));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteTaskResponse> deleteTask(@PathVariable Long id) {
        DeleteTaskResponse result = tasksGatewayService.deleteTask(id);
        if (result.deleted() && !result.degraded()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/unstable")
    public ResponseEntity<UnstableResponse> callUnstable(@RequestParam String mode) {
        return ResponseEntity.ok(tasksGatewayService.callUnstable(mode));
    }
}

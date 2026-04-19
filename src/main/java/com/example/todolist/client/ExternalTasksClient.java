package com.example.todolist.client;

import com.example.todolist.dto.TaskCreateRequest;
import com.example.todolist.dto.TaskCreateResult;
import com.example.todolist.dto.TaskResponse;
import com.example.todolist.exception.ExternalApiException;
import com.example.todolist.exception.TaskNotFoundException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ExternalTasksClient {

    private static final Logger log = LoggerFactory.getLogger(ExternalTasksClient.class);
    private static final int LOG_BODY_LIMIT = 300;
    private static final TypeReference<List<TaskResponse>> TASK_LIST_TYPE = new TypeReference<>() { };

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public ExternalTasksClient(RestClient externalRestClient, ObjectMapper objectMapper) {
        this.restClient = externalRestClient;
        this.objectMapper = objectMapper;
    }

    public TaskCreateResult createTask(TaskCreateRequest request) {
        return restClient.post()
                .uri("/external/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange((clientRequest, clientResponse) -> {
                    HttpStatusCode status = clientResponse.getStatusCode();
                    byte[] body = StreamUtils.copyToByteArray(clientResponse.getBody());

                    if (status.value() == HttpStatus.CREATED.value()) {
                        URI location = clientResponse.getHeaders().getLocation();
                        TaskResponse task = tryReadTaskJson(body, clientResponse.getHeaders().getContentType(), status);
                        return new TaskCreateResult(task, location);
                    }
                    throwForErrorStatus(status, body);
                });
    }

    public TaskResponse getTask(Long id) {
        return restClient.get()
                .uri("/external/v1/tasks/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange((clientRequest, clientResponse) -> {
                    HttpStatusCode status = clientResponse.getStatusCode();
                    byte[] body = StreamUtils.copyToByteArray(clientResponse.getBody());

                    if (status.is2xxSuccessful()) {
                        return tryReadTaskJson(body, clientResponse.getHeaders().getContentType(), status);
                    }
                    throwForErrorStatus(status, body);
                    throw new ExternalApiException("Unexpected external status " + status.value());
                });
    }

    public List<TaskResponse> listTasks(Boolean completed, Integer limit) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/external/v1/tasks")
                        .queryParamIfPresent("completed", Optional.ofNullable(completed))
                        .queryParamIfPresent("limit", Optional.ofNullable(limit))
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange((clientRequest, clientResponse) -> {
                    HttpStatusCode status = clientResponse.getStatusCode();
                    byte[] body = StreamUtils.copyToByteArray(clientResponse.getBody());

                    if (status.is2xxSuccessful()) {
                        MediaType contentType = clientResponse.getHeaders().getContentType();
                        if (contentType == null || !MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
                            logUnexpectedBody(status, contentType, body);
                            throw new ExternalApiException("Unexpected content type from external API: " + contentType);
                        }
                        try {
                            return objectMapper.readValue(body, TASK_LIST_TYPE);
                        } catch (IOException ex) {
                            throw new ExternalApiException("Failed to parse tasks list response", ex);
                        }
                    }
                    throwForErrorStatus(status, body);
                    throw new ExternalApiException("Unexpected external status " + status.value());
                });
    }

    public void deleteTask(Long id) {
        HttpStatusCode status = restClient.delete()
                .uri("/external/v1/tasks/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(statusCode -> statusCode.value() == HttpStatus.NOT_FOUND.value(),
                        (request, response) -> {
                            byte[] body = StreamUtils.copyToByteArray(response.getBody());
                            throw new TaskNotFoundException(problemMessage(body));
                        })
                .onStatus(statusCode -> statusCode.value() == HttpStatus.TOO_MANY_REQUESTS.value(),
                        (request, response) -> {
                            throw new ExternalApiException("External API throttled request with HTTP 429");
                        })
                .onStatus(HttpStatusCode::is5xxServerError,
                        (request, response) -> {
                            throw new ExternalApiException("External API failed with status " + response.getStatusCode().value());
                        })
                .toBodilessEntity()
                .getStatusCode();

        if (status.value() != HttpStatus.NO_CONTENT.value()) {
            throw new ExternalApiException("Expected 204 from external API, got " + status.value());
        }
    }

    public String callUnstable(String mode) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/external/v1/unstable")
                        .queryParam("mode", mode)
                        .build())
                .accept(MediaType.APPLICATION_JSON, MediaType.TEXT_HTML)
                .exchange((clientRequest, clientResponse) -> {
                    HttpStatusCode status = clientResponse.getStatusCode();
                    byte[] body = StreamUtils.copyToByteArray(clientResponse.getBody());
                    if (status.is2xxSuccessful()) {
                        return new String(body, StandardCharsets.UTF_8);
                    }
                    MediaType contentType = clientResponse.getHeaders().getContentType();
                    if (contentType == null || !MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
                        logUnexpectedBody(status, contentType, body);
                    }
                    throwForErrorStatus(status, body);
                    throw new ExternalApiException("Unexpected unstable endpoint status " + status.value());
                });
    }

    private TaskResponse tryReadTaskJson(byte[] body, MediaType contentType, HttpStatusCode status) {
        if (contentType == null || !MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
            logUnexpectedBody(status, contentType, body);
            throw new ExternalApiException("Unexpected content type from external API: " + contentType);
        }
        try {
            return objectMapper.readValue(body, TaskResponse.class);
        } catch (IOException ex) {
            throw new ExternalApiException("Failed to parse task response", ex);
        }
    }

    private String problemMessage(byte[] body) {
        try {
            Map<String, Object> map = objectMapper.readValue(body, new TypeReference<>() { });
            Object detail = map.get("detail");
            if (detail == null) {
                return "Task was not found in external API";
            }
            return String.valueOf(detail);
        } catch (Exception ex) {
            return "Task was not found in external API";
        }
    }

    private void throwForErrorStatus(HttpStatusCode status, byte[] body) {
        if (status.value() == HttpStatus.NOT_FOUND.value()) {
            throw new TaskNotFoundException(problemMessage(body));
        }
        if (status.value() == HttpStatus.TOO_MANY_REQUESTS.value()) {
            throw new ExternalApiException("External API throttled request with HTTP 429");
        }
        if (status.is5xxServerError()) {
            throw new ExternalApiException("External API failed with status " + status.value());
        }
        throw new ExternalApiException("Unexpected external status " + status.value());
    }

    private void logUnexpectedBody(HttpStatusCode status, MediaType contentType, byte[] body) {
        String snippet = new String(body, StandardCharsets.UTF_8);
        if (snippet.length() > LOG_BODY_LIMIT) {
            snippet = snippet.substring(0, LOG_BODY_LIMIT) + "...";
        }
        log.warn("Unexpected content type from external API status={} contentType={} bodySnippet={}",
                status.value(), contentType, snippet.replaceAll("[\\r\\n]+", " "));
    }
}

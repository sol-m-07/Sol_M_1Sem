package com.example.todolist.config;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.time.Instant;
import java.util.UUID;

/**
 * Бин с областью request: для каждого HTTP-запроса создаётся новый экземпляр.
 * Хранит requestId и время начала обработки запроса.
 */
@Component
@RequestScope
public class RequestScopedBean {

    private final String requestId = UUID.randomUUID().toString();
    private final Instant requestStartTime = Instant.now();

    public String getRequestId() {
        return requestId;
    }

    public Instant getRequestStartTime() {
        return requestStartTime;
    }
}

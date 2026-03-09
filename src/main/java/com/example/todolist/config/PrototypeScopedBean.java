package com.example.todolist.config;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Бин с областью prototype: при каждом обращении создаётся новый экземпляр.
 * Генерирует уникальный идентификатор для задачи на основе UUID.
 */
@Component
@Scope("prototype")
public class PrototypeScopedBean {

    private final String taskId = UUID.randomUUID().toString();

    /**
     * Возвращает уникальный ID задачи, сгенерированный при создании бина.
     */
    public String getTaskId() {
        return taskId;
    }
}

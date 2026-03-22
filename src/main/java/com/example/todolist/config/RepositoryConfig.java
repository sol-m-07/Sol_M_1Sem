package com.example.todolist.config;

import com.example.todolist.repository.StubTaskRepository;
import com.example.todolist.repository.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация бинов. StubTaskRepository создаётся через @Bean.
 */
@Configuration
public class RepositoryConfig {

    @Bean(name = "stubTaskRepository")
    public TaskRepository stubTaskRepository() {
        return new StubTaskRepository();
    }
}

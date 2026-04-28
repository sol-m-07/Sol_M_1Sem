package com.example.todolist.repository;

import com.example.todolist.config.JpaAuditingConfig;
import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaAuditingConfig.class)
@ActiveProfiles("test")
class TaskRepositoryIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("todolist_it")
            .withUsername("test_user")
            .withPassword("test_pass");

    @DynamicPropertySource
    static void registerDataSourceProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void cleanState() {
        taskRepository.deleteAll();
    }

    @Test
    @DisplayName("PostgreSQL container starts up and is reachable")
    void containerIsRunning() {
        assertThat(POSTGRES.isRunning()).isTrue();
        assertThat(POSTGRES.getJdbcUrl()).startsWith("jdbc:postgresql://");
    }

    @Test
    @DisplayName("findTasksDueSoon (custom @Query) returns only tasks with dueDate within window")
    void findTasksDueSoon_returnsOnlyTasksWithinWindow() {
        Task dueIn3Days = new Task();
        dueIn3Days.setTitle("Due in 3 days");
        dueIn3Days.setPriority(Priority.HIGH);
        dueIn3Days.setDueDate(LocalDate.now().plusDays(3));
        taskRepository.save(dueIn3Days);

        Task dueIn30Days = new Task();
        dueIn30Days.setTitle("Due in 30 days");
        dueIn30Days.setPriority(Priority.LOW);
        dueIn30Days.setDueDate(LocalDate.now().plusDays(30));
        taskRepository.save(dueIn30Days);

        Task noDueDate = new Task();
        noDueDate.setTitle("No due date");
        noDueDate.setPriority(Priority.MEDIUM);
        taskRepository.save(noDueDate);

        List<Task> dueSoon = taskRepository.findTasksDueSoon(LocalDate.now().plusDays(7));

        assertThat(dueSoon)
                .hasSize(1)
                .extracting(Task::getTitle)
                .containsExactly("Due in 3 days");
    }
}

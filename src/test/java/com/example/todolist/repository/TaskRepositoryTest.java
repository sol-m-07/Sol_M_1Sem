package com.example.todolist.repository;

import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import com.example.todolist.model.TaskAttachment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskAttachmentRepository attachmentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        attachmentRepository.deleteAll();
        taskRepository.deleteAll();
    }

    @Test
    @DisplayName("save and findById returns persisted task")
    void saveAndFindById() {
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Description");
        task.setPriority(Priority.MEDIUM);
        task.setTags(Set.of("tag1", "tag2"));

        Task saved = taskRepository.save(task);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();

        Task found = taskRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getTitle()).isEqualTo("Test Task");
        assertThat(found.getTags()).containsExactlyInAnyOrder("tag1", "tag2");
    }

    @Test
    @DisplayName("findByCompletedAndPriority returns matching tasks")
    void findByCompletedAndPriority() {
        Task highNotDone = new Task();
        highNotDone.setTitle("High priority");
        highNotDone.setCompleted(false);
        highNotDone.setPriority(Priority.HIGH);
        taskRepository.save(highNotDone);

        Task lowDone = new Task();
        lowDone.setTitle("Low done");
        lowDone.setCompleted(true);
        lowDone.setPriority(Priority.LOW);
        taskRepository.save(lowDone);

        List<Task> results = taskRepository.findByCompletedAndPriority(false, Priority.HIGH);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("High priority");
    }

    @Test
    @DisplayName("findTasksDueSoon returns tasks due within date range")
    void findTasksDueSoon() {
        Task dueSoon = new Task();
        dueSoon.setTitle("Due soon");
        dueSoon.setPriority(Priority.MEDIUM);
        dueSoon.setDueDate(LocalDate.now().plusDays(3));
        taskRepository.save(dueSoon);

        Task dueLater = new Task();
        dueLater.setTitle("Due later");
        dueLater.setPriority(Priority.LOW);
        dueLater.setDueDate(LocalDate.now().plusDays(30));
        taskRepository.save(dueLater);

        Task noDue = new Task();
        noDue.setTitle("No due date");
        noDue.setPriority(Priority.HIGH);
        taskRepository.save(noDue);

        List<Task> results = taskRepository.findTasksDueSoon(LocalDate.now().plusDays(7));

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("Due soon");
    }

    @Test
    @DisplayName("findAllWithAttachments loads attachments eagerly (N+1 solution)")
    void findAllWithAttachments() {
        Task task = new Task();
        task.setTitle("With attachment");
        task.setPriority(Priority.MEDIUM);
        task = taskRepository.save(task);

        TaskAttachment attachment = new TaskAttachment();
        attachment.setTask(task);
        attachment.setFileName("file.txt");
        attachment.setStoredFileName("stored_file.txt");
        attachment.setContentType("text/plain");
        attachment.setSize(100);
        attachment.setUploadedAt(LocalDateTime.now());
        attachmentRepository.save(attachment);

        entityManager.flush();
        entityManager.clear();

        List<Task> results = taskRepository.findAllWithAttachments();

        assertThat(results).isNotEmpty();
        Task loaded = results.stream()
                .filter(t -> t.getTitle().equals("With attachment"))
                .findFirst().orElseThrow();
        assertThat(loaded.getAttachments()).hasSize(1);
        assertThat(loaded.getAttachments().get(0).getFileName()).isEqualTo("file.txt");
    }

    @Test
    @DisplayName("save task with tags persists tags in task_tags table")
    void saveWithTags() {
        Task task = new Task();
        task.setTitle("Tagged task");
        task.setPriority(Priority.LOW);
        task.setTags(Set.of("java", "spring"));
        task = taskRepository.save(task);

        Task found = taskRepository.findById(task.getId()).orElseThrow();
        assertThat(found.getTags()).containsExactlyInAnyOrder("java", "spring");
    }

    @Test
    @DisplayName("delete task cascades to attachments")
    void cascadeDeleteRemovesAttachments() {
        Task task = new Task();
        task.setTitle("To delete");
        task.setPriority(Priority.MEDIUM);
        task = taskRepository.save(task);

        TaskAttachment attachment = new TaskAttachment();
        attachment.setTask(task);
        attachment.setFileName("att.txt");
        attachment.setStoredFileName("stored_att.txt");
        attachment.setContentType("text/plain");
        attachment.setSize(50);
        attachment.setUploadedAt(LocalDateTime.now());
        attachmentRepository.save(attachment);

        entityManager.flush();
        entityManager.clear();

        taskRepository.deleteById(task.getId());
        entityManager.flush();

        assertThat(attachmentRepository.findByTaskId(task.getId())).isEmpty();
    }
}

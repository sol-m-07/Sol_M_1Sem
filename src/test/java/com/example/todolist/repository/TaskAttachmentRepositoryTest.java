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
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskAttachmentRepositoryTest {

    @Autowired
    private TaskAttachmentRepository attachmentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        attachmentRepository.deleteAll();
        taskRepository.deleteAll();
    }

    private Task createTask(String title) {
        Task task = new Task();
        task.setTitle(title);
        task.setPriority(Priority.MEDIUM);
        return taskRepository.save(task);
    }

    private TaskAttachment createAttachment(Task task, String fileName) {
        TaskAttachment attachment = new TaskAttachment();
        attachment.setTask(task);
        attachment.setFileName(fileName);
        attachment.setStoredFileName("stored_" + fileName);
        attachment.setContentType("text/plain");
        attachment.setSize(100);
        attachment.setUploadedAt(LocalDateTime.now());
        return attachmentRepository.save(attachment);
    }

    @Test
    @DisplayName("save and findById returns persisted attachment")
    void saveAndFindById() {
        Task task = createTask("Test");
        TaskAttachment saved = createAttachment(task, "doc.pdf");

        Optional<TaskAttachment> found = attachmentRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getFileName()).isEqualTo("doc.pdf");
        assertThat(found.get().getTask().getId()).isEqualTo(task.getId());
    }

    @Test
    @DisplayName("findByTaskId returns all attachments for a task")
    void findByTaskId() {
        Task task = createTask("Multi-attachment");
        createAttachment(task, "file1.txt");
        createAttachment(task, "file2.txt");

        Task otherTask = createTask("Other");
        createAttachment(otherTask, "other.txt");

        List<TaskAttachment> results = attachmentRepository.findByTaskId(task.getId());

        assertThat(results).hasSize(2);
        assertThat(results).extracting(TaskAttachment::getFileName)
                .containsExactlyInAnyOrder("file1.txt", "file2.txt");
    }

    @Test
    @DisplayName("findByTaskId returns empty list for task with no attachments")
    void findByTaskId_empty() {
        Task task = createTask("No attachments");

        List<TaskAttachment> results = attachmentRepository.findByTaskId(task.getId());

        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("deleteById removes attachment")
    void deleteById() {
        Task task = createTask("Test");
        TaskAttachment att = createAttachment(task, "todelete.txt");

        attachmentRepository.deleteById(att.getId());

        assertThat(attachmentRepository.findById(att.getId())).isEmpty();
    }
}

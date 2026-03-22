package com.example.todolist.service;

import com.example.todolist.exception.AttachmentNotFoundException;
import com.example.todolist.exception.TaskNotFoundException;
import com.example.todolist.model.Task;
import com.example.todolist.model.TaskAttachment;
import com.example.todolist.repository.InMemoryTaskAttachmentRepository;
import com.example.todolist.repository.InMemoryTaskRepository;
import com.example.todolist.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AttachmentServiceTest {

    @TempDir
    Path tempDir;

    private AttachmentService attachmentService;
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() throws IOException {
        taskRepository = new InMemoryTaskRepository();
        attachmentService = new AttachmentService(
                new InMemoryTaskAttachmentRepository(),
                taskRepository,
                tempDir.toString()
        );
        attachmentService.init();
    }

    @Test
    @DisplayName("storeAttachment saves file and metadata")
    void storeAttachment_success() throws IOException {
        Task task = taskRepository.save(new Task(null, "T", "D", false));
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "Hello".getBytes());

        TaskAttachment result = attachmentService.storeAttachment(task.getId(), file);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getFileName()).isEqualTo("test.txt");
        assertThat(result.getSize()).isEqualTo(5);
        assertThat(result.getContentType()).isEqualTo("text/plain");
    }

    @Test
    @DisplayName("storeAttachment throws when task not found")
    void storeAttachment_taskNotFound() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "data".getBytes());
        assertThatThrownBy(() -> attachmentService.storeAttachment(999L, file))
                .isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    @DisplayName("loadAsResource returns readable resource")
    void loadAsResource_success() throws IOException {
        Task task = taskRepository.save(new Task(null, "T", "D", false));
        MockMultipartFile file = new MockMultipartFile(
                "file", "res.txt", "text/plain", "content".getBytes());
        TaskAttachment att = attachmentService.storeAttachment(task.getId(), file);

        Resource resource = attachmentService.loadAsResource(att.getId());
        assertThat(resource.exists()).isTrue();
        assertThat(resource.isReadable()).isTrue();
    }

    @Test
    @DisplayName("getAttachment throws when not found")
    void getAttachment_notFound() {
        assertThatThrownBy(() -> attachmentService.getAttachment(999L))
                .isInstanceOf(AttachmentNotFoundException.class);
    }

    @Test
    @DisplayName("deleteAttachment removes file and metadata")
    void deleteAttachment_success() throws IOException {
        Task task = taskRepository.save(new Task(null, "T", "D", false));
        MockMultipartFile file = new MockMultipartFile(
                "file", "del.txt", "text/plain", "data".getBytes());
        TaskAttachment att = attachmentService.storeAttachment(task.getId(), file);

        attachmentService.deleteAttachment(att.getId());
        assertThatThrownBy(() -> attachmentService.getAttachment(att.getId()))
                .isInstanceOf(AttachmentNotFoundException.class);
    }
}

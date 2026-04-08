package com.example.todolist.service;

import com.example.todolist.exception.AttachmentNotFoundException;
import com.example.todolist.exception.TaskNotFoundException;
import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import com.example.todolist.model.TaskAttachment;
import com.example.todolist.repository.TaskAttachmentRepository;
import com.example.todolist.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class AttachmentServiceTest {

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskAttachmentRepository attachmentRepository;

    @BeforeEach
    void setUp() {
        attachmentRepository.deleteAll();
        taskRepository.deleteAll();
    }

    private Task createTask() {
        Task task = new Task();
        task.setTitle("Task for attachment");
        task.setDescription("Description");
        task.setPriority(Priority.MEDIUM);
        return taskRepository.save(task);
    }

    @Test
    @DisplayName("storeAttachment saves file and metadata")
    void storeAttachment_success() throws IOException {
        Task task = createTask();
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
        Task task = createTask();
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
        Task task = createTask();
        MockMultipartFile file = new MockMultipartFile(
                "file", "del.txt", "text/plain", "data".getBytes());
        TaskAttachment att = attachmentService.storeAttachment(task.getId(), file);

        attachmentService.deleteAttachment(att.getId());
        assertThatThrownBy(() -> attachmentService.getAttachment(att.getId()))
                .isInstanceOf(AttachmentNotFoundException.class);
    }
}

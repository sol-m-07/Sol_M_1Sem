package com.example.todolist.repository;

import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByCompletedAndPriority(boolean completed, Priority priority);

    @Query("SELECT t FROM Task t WHERE t.dueDate IS NOT NULL "
            + "AND t.dueDate BETWEEN CURRENT_DATE AND :endDate")
    List<Task> findTasksDueSoon(@Param("endDate") LocalDate endDate);

    @EntityGraph(attributePaths = {"attachments"})
    @Query("SELECT DISTINCT t FROM Task t")
    List<Task> findAllWithAttachments();
}

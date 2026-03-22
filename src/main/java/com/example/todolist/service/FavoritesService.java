package com.example.todolist.service;

import com.example.todolist.exception.TaskNotFoundException;
import com.example.todolist.model.Task;
import com.example.todolist.repository.TaskRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class FavoritesService {

    private static final String SESSION_ATTR = "favoriteTaskIds";

    private final TaskRepository taskRepository;

    public FavoritesService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @SuppressWarnings("unchecked")
    public void addFavorite(HttpSession session, Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new TaskNotFoundException(taskId);
        }
        Set<Long> favorites = (Set<Long>) session.getAttribute(SESSION_ATTR);
        if (favorites == null) {
            favorites = new LinkedHashSet<>();
        }
        favorites.add(taskId);
        session.setAttribute(SESSION_ATTR, favorites);
    }

    @SuppressWarnings("unchecked")
    public void removeFavorite(HttpSession session, Long taskId) {
        Set<Long> favorites = (Set<Long>) session.getAttribute(SESSION_ATTR);
        if (favorites != null) {
            favorites.remove(taskId);
            session.setAttribute(SESSION_ATTR, favorites);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Task> getFavorites(HttpSession session) {
        Set<Long> favorites = (Set<Long>) session.getAttribute(SESSION_ATTR);
        if (favorites == null || favorites.isEmpty()) {
            return List.of();
        }
        List<Task> result = new ArrayList<>();
        for (Long id : favorites) {
            taskRepository.findById(id).ifPresent(result::add);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public Set<Long> getFavoriteIds(HttpSession session) {
        Set<Long> favorites = (Set<Long>) session.getAttribute(SESSION_ATTR);
        return favorites != null ? favorites : Set.of();
    }
}

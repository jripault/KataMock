package org.codingdojo.service;

import org.codingdojo.domain.Task;

import java.util.List;

public interface TaskService {

    Task save(Task task);

    List<Task> findAll();

    Task findById(Long id);

    List<Task> findByTitle(String title);

    void delete(Long id);

    Task assignTaskToUser(Long taskId, Long userId);

    void assignTasksToUser(List<Long> taskIds, Long userId);
}

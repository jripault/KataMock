package org.codingdojo.service.impl;

import org.codingdojo.domain.Task;
import org.codingdojo.domain.User;
import org.codingdojo.exception.ResourceNotFoundException;
import org.codingdojo.exception.TaskNotAssignableException;
import org.codingdojo.repository.TaskRepository;
import org.codingdojo.service.NotificationService;
import org.codingdojo.service.TaskService;
import org.codingdojo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.DayOfWeek;
import java.util.List;

@Transactional
@Service
public class TaskServiceImpl implements TaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, UserService userService, NotificationService notificationService) {
        Assert.notNull(taskRepository, "taskRepository must be not null");
        Assert.notNull(userService, "userService must be not null");
        Assert.notNull(notificationService, "notificationService must be not null");
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @Override
    public Task save(Task task) {
        Assert.notNull(task, "task should be not null");
        return taskRepository.save(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Task findById(Long id) {
        Assert.notNull(id, "id should be not null");
        Task task = taskRepository.findOne(id);
        if (task == null) {
            throw new ResourceNotFoundException(String.format("Task with id: %s not found", id));
        }
        return task;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> findByTitle(String title) {
        Assert.notNull(title, "title should be not null");
        return taskRepository.findByTitle(title);
    }

    @Override
    public void delete(Long id) {
        Assert.notNull(id, "id should be not null");
        taskRepository.delete(id);
    }

    @Override
    public Task assignTaskToUser(Long taskId, Long userId) {
        return null;
    }

    @Override
    public void assignTasksToUser(List<Long> taskIds, Long userId) {

    }

    protected void sendNotifications(Task task, User user, boolean assignation) {
        if (user != null) {
            if (user.getEmail() != null) {
                this.notificationService.send(user.getEmail(), String.format("The task: '%s' has been assigned to %s.", task.getTitle(), assignation ? "you" : "somebody else"));
            } else {
                throw new RuntimeException(String.format("No email for user: %s", user.getName()));
            }
        }
    }
}

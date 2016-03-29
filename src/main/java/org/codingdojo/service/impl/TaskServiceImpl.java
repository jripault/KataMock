package org.codingdojo.service.impl;

import org.codingdojo.domain.Task;
import org.codingdojo.domain.User;
import org.codingdojo.exception.ResourceNotFoundException;
import org.codingdojo.repository.TaskRepository;
import org.codingdojo.service.NotificationService;
import org.codingdojo.service.TaskService;
import org.codingdojo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Transactional
@Service
public class TaskServiceImpl implements TaskService {

    private final UserService userService;
    private final TaskRepository taskRepository;
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
        return taskRepository.findByTitle(title);
    }

    @Override
    public void delete(Long id) {
        Assert.notNull(id, "id should be not null");
        taskRepository.delete(id);
    }

    @Override
    public Task assignTaskToUser(Long taskId, Long userId) {
        Assert.notNull(taskId, "taskId should be not null");
        Assert.notNull(userId, "userId should be not null");

        Task task = this.findById(taskId);
        Assert.isTrue(task.isAssignable(), "task is not assignable (done or overdue)");

        User user = this.userService.findById(userId);

        User previousUser = task.getUser();
        task.setUser(user);

        sendNotifications(task, user, true);
        sendNotifications(task, previousUser, false);
        return task;
    }

    private void sendNotifications(Task task, User user, boolean assignation) {
        if (user.getEmail() != null) {
            this.notificationService.send(user.getEmail(), "The task: '" + task.getTitle() + (assignation ?  "' has been assigned to you" : "' has been unassigned"));
        } else{
            throw new RuntimeException("No email for user" + user.getName());
        }
    }
}

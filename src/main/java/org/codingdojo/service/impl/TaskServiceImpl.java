package org.codingdojo.service.impl;

import org.codingdojo.domain.Task;
import org.codingdojo.domain.User;
import org.codingdojo.repository.TaskRepository;
import org.codingdojo.repository.UserRepository;
import org.codingdojo.service.NotificationService;
import org.codingdojo.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Transactional
@Service
public class TaskServiceImpl implements TaskService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final NotificationService notificationService;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository, NotificationService notificationService) {
        Assert.notNull(taskRepository, "taskRepository must be not null");
        Assert.notNull(userRepository, "userRepository must be not null");
        Assert.notNull(notificationService, "notificationService must be not null");
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
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
        return taskRepository.findOne(id);
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

        Task task = this.taskRepository.findOne(taskId);
        Assert.notNull(task, "taskId should correspond to a valid task");
        Assert.isTrue(task.isAssignable(), "task is not assignable (done or overdue)");

        User user = this.userRepository.findOne(userId);
        Assert.notNull(user, "userId should correspond to a valid newUser");

        User previousUser = task.getUser();
        task.setUser(user);

        sendNotifications(task, user, previousUser);
        return task;
    }

    private void sendNotifications(Task task, User newUser, User previousUser) {
        if ((previousUser != null) && (previousUser != newUser) && (previousUser.getEmail() != null)) {
            this.notificationService.send(previousUser.getEmail(), "The task: '" + task.getTitle() + "' has been reassigned");
        }

        if (newUser.getEmail() != null) {
            this.notificationService.send(newUser.getEmail(), "The task: '" + task.getTitle() + "' has been assigned to you");
        }
    }
}

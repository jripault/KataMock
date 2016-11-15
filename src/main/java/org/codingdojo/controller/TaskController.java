package org.codingdojo.controller;

import org.codingdojo.domain.Task;
import org.codingdojo.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/task")
public class TaskController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        Assert.notNull(taskService, "taskService must be not null");
        this.taskService = taskService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Task create(@RequestBody Task task) {
        task = taskService.save(task);
        LOGGER.debug("Task created: {}", task);
        return task;
    }

    @GetMapping
    public List<Task> findAll() {
        List<Task> tasks = taskService.findAll();
        LOGGER.debug("Task(s): {} (nb: {})", tasks, tasks.size());
        return tasks;
    }

    @GetMapping(path = "{id}")
    public Task findById(@PathVariable Long id) {
        Task task = taskService.findById(id);
        LOGGER.debug("Task with id '{}' found: {}", id, task);
        return task;
    }

    @GetMapping(path = "title/{title}")
    public List<Task> findByTitle(@PathVariable String title) {
        List<Task> tasks = taskService.findByTitle(title);
        LOGGER.debug("Task(s) found with title '{}': {} (nb: {})", title, tasks, tasks.size());
        return tasks;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "{id}")
    public void delete(@PathVariable Long id) {
        taskService.delete(id);
        LOGGER.debug("Task with id '{}' was deleted", id);
    }

    @PutMapping(path = "{taskId}/user/{userId}")
    public Task assignTaskToUser(@PathVariable Long taskId, @PathVariable Long userId) {
        Task task = taskService.assignTaskToUser(taskId, userId);
        LOGGER.debug("Task with id '{}' was assigned to user with id '{}'", taskId, userId);
        return task;
    }
}

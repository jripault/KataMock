package org.codingdojo.controller;

import lombok.extern.slf4j.Slf4j;
import org.codingdojo.domain.Task;
import org.codingdojo.exception.ResourceNotFoundException;
import org.codingdojo.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/api/task")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        Assert.notNull(taskService, "taskService must be not null");
        this.taskService = taskService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public Task create(@RequestBody Task task) {
        return taskService.save(task);
    }

    @RequestMapping
    public List<Task> findAll() {
        List<Task> tasks = taskService.findAll();
        log.debug("Task(s): {} (nb: {})", tasks, tasks.size());
        return tasks;
    }

    @RequestMapping(path = "{id}")
    public Task findById(@PathVariable Long id) {
        Task task = taskService.findById(id);
        if (task == null) {
            throw new ResourceNotFoundException(String.format("Task with id: %s not found", id));
        }

        log.debug("Task with id '{}' found: {}", id, task);
        return task;
    }

    @RequestMapping(path = "title/{title}")
    public List<Task> findByTitle(@PathVariable String title) {
        List<Task> tasks = taskService.findByTitle(title);
        log.debug("Task(s) found with title '{}': {} (nb: {})", title, tasks, tasks.size());
        return tasks;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(path = "{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable Long id) {
        taskService.delete(id);
    }

    @RequestMapping(path = "{taskId}/user/{userId}", method = RequestMethod.PUT)
    public Task assignTaskToUser(@PathVariable Long taskId, @PathVariable Long userId) {
        return taskService.assignTaskToUser(taskId, userId);
    }
}

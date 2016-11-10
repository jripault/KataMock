package org.codingdojo.domain;

import java.time.LocalDateTime;

public class TaskBuilder {

    private LocalDateTime creationDate = LocalDateTime.now();
    private Long id;
    private String title;
    private String description;
    private User user;
    private LocalDateTime deadLine;
    private boolean done = false;

    private TaskBuilder() {
    }

    public static TaskBuilder aTask() {
        return new TaskBuilder();
    }

    public TaskBuilder creationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public TaskBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public TaskBuilder title(String title) {
        this.title = title;
        return this;
    }

    public TaskBuilder description(String description) {
        this.description = description;
        return this;
    }

    public TaskBuilder user(User user) {
        this.user = user;
        return this;
    }

    public TaskBuilder deadLine(LocalDateTime deadLine) {
        this.deadLine = deadLine;
        return this;
    }

    public TaskBuilder done(boolean done) {
        this.done = done;
        return this;
    }

    public TaskBuilder but() {
        return aTask().creationDate(creationDate).id(id).title(title).description(description).user(user).deadLine(deadLine).done(done);
    }

    public Task build() {
        Task task = new Task();
        task.setCreationDate(creationDate);
        task.setId(id);
        task.setTitle(title);
        task.setDescription(description);
        task.setUser(user);
        task.setDeadLine(deadLine);
        task.setDone(done);
        return task;
    }
}

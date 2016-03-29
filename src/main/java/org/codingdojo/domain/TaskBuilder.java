package org.codingdojo.domain;

import java.time.LocalDateTime;

/**
 * User: JRI <julien.ripault@atos.net>
 * Date: 29/03/2016
 */
public class TaskBuilder {
    private User user;
    private LocalDateTime deadLine;
    private String description;
    private boolean done = false;
    private Long id;
    private String title;

    private TaskBuilder() {
    }

    public static TaskBuilder aTask() {
        return new TaskBuilder();
    }

    public TaskBuilder user(User user) {
        this.user = user;
        return this;
    }

    public TaskBuilder deadLine(LocalDateTime deadLine) {
        this.deadLine = deadLine;
        return this;
    }

    public TaskBuilder description(String description) {
        this.description = description;
        return this;
    }

    public TaskBuilder done(boolean done) {
        this.done = done;
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

    public TaskBuilder but() {
        return aTask().user(user).deadLine(deadLine).description(description).done(done).id(id).title(title);
    }

    public Task build() {
        Task task = new Task();
        task.setUser(user);
        task.setDeadLine(deadLine);
        task.setDescription(description);
        task.setDone(done);
        task.setId(id);
        task.setTitle(title);
        return task;
    }
}

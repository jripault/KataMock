package org.codingdojo.domain;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

@Entity
public class Task {

    @Id
    @GeneratedValue
    private Long id;

    @NotEmpty
    private String title;

    private String description;

    @ManyToOne
    private User user;

    private LocalDateTime creationDate = LocalDateTime.now();

    private LocalDateTime deadLine;

    private boolean done = false;

    @Transient
    private boolean overdue;

    public boolean isOverdue() {
        return !done &&
                (deadLine != null) &&
                now().isAfter(deadLine);
    }

    public boolean isAssignable() {
        return !(done || isOverdue());
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public LocalDateTime getDeadLine() {
        return deadLine;
    }

    public String getDescription() {
        return description;
    }

    public boolean isDone() {
        return done;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public User getUser() {
        return user;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void setDeadLine(LocalDateTime deadLine) {
        this.deadLine = deadLine;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOverdue(boolean overdue) {
        this.overdue = overdue;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

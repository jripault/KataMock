package org.codingdojo.domain;

import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

@Entity
@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = "user")
public class Task {

    @Id
    @GeneratedValue
    private Long id;

    @NotEmpty
    private String title;

    private String description;

    @ManyToOne
    private User user;

    @Setter(AccessLevel.NONE)
    private LocalDateTime creationDate = LocalDateTime.now();

    private LocalDateTime deadLine;

    private boolean done = false;

    @Transient
    @Setter(AccessLevel.NONE)
    private boolean overdue;

    public boolean isOverdue() {
        return !done &&
                (deadLine != null) &&
                now().isAfter(deadLine);
    }

    public boolean isAssignable() {
        return !(isDone() || isOverdue());
    }

}

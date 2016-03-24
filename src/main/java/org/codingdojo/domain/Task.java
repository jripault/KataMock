package org.codingdojo.domain;

import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Date;

import static java.time.LocalDateTime.*;
import static java.time.ZoneId.systemDefault;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    @Future
    @Temporal(TemporalType.TIMESTAMP)
    private Date deadLine;

    private boolean done = false;

    @Transient
    @Setter(AccessLevel.NONE)
    private boolean overdue;

    public boolean isOverdue() {
        return !done &&
                (deadLine != null) &&
                now().isAfter(ofInstant(deadLine.toInstant(), systemDefault()));
    }

    public boolean isAssignable() {
        return !(isDone() || isOverdue());
    }
}

package org.codingdojo.repository;

import org.assertj.core.api.Assertions;
import org.codingdojo.domain.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.codingdojo.domain.TaskBuilder.aTask;

@RunWith(SpringRunner.class)
@DataJpaTest(showSql = false)
public class TaskRepositoryIT {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository repository;

    @Test
    public void shouldCreateValidTask() {
        // Given
        Task task = aTask().title("taskTitle").description("description").creationDate(now()).deadLine(now()).build();

        // When
        repository.save(task);

        // Then
        assertThat(repository.exists(task.getId())).isTrue();
    }

    @Test(expected = ConstraintViolationException.class)
    public void shouldThrowConstraintViolationExceptionWhenCreateInvalidTaskNoTitle() {
        // Given
        Task task = aTask().build();

        // When
        repository.save(task);

        // Then
    }

    @Test
    public void shouldFindTaskByTitle() {
        // Given
        Task task1 = aTask().title("taskTitle1").description("description1").deadLine(now()).build();
        entityManager.persist(task1);
        Task task2 = aTask().title("taskTitle2").description("description2").deadLine(now()).build();
        entityManager.persist(task2);
        Task task3 = aTask().title("taskTitle3").description("description3").deadLine(now()).build();
        entityManager.persist(task3);

        // When
        List<Task> actualTasks = repository.findByTitle(task1.getTitle());

        // Then
        Assertions.assertThat(actualTasks).hasSize(1).containsOnly(task1);
    }

    @Test
    public void shouldFindAllTasksWithSameTitle() {
        // Given
        Task task1 = aTask().title("taskTitle").description("description1").deadLine(now()).build();
        entityManager.persist(task1);
        Task task2 = aTask().title("taskTitle2").description("description2").deadLine(now()).build();
        entityManager.persist(task2);
        Task task3 = aTask().title("taskTitle").description("description3").deadLine(now()).build();
        entityManager.persist(task3);

        // When
        List<Task> actualTasks = repository.findByTitle(task1.getTitle());

        // Then
        Assertions.assertThat(actualTasks).hasSize(2).containsOnly(task1, task3);
    }
}
package org.codingdojo.controller;

import org.assertj.core.api.Assertions;
import org.codingdojo.domain.Task;
import org.codingdojo.exception.ResourceNotFoundException;
import org.codingdojo.exception.TaskNotAssignableException;
import org.codingdojo.service.impl.TaskServiceImpl;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.time.LocalDateTime.now;
import static org.codingdojo.domain.TaskBuilder.aTask;
import static org.mockito.Mockito.*;

/**
 * An example of mock misuse: {@link TaskServiceImpl} class is "mocked", which is the only code of the {@link TaskController} !
 */
@RunWith(MockitoJUnitRunner.class)
public class TaskControllerTest {

    @InjectMocks
    private TaskController controller;

    @Mock
    private TaskServiceImpl taskService;

    @Test
    public void shouldCreateValidTask() {
        // Given
        Task task = aTask().title("taskTitle1").description("description1").deadLine(now()).build();

        // When
        controller.create(task);

        // Then
        verify(taskService, times(1)).save(task);
    }

    @Test(expected = ConstraintViolationException.class)
    public void shouldThrowConstraintViolationExceptionWhenCreateInvalidTaskNoTitle() {
        // Given
        when(taskService.save(any(Task.class))).thenThrow(ConstraintViolationException.class);

        // When
        controller.create(new Task());

        // Then
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundExceptionWhenFindUnknownTask() {
        // Given
        when(taskService.findById(-1L)).thenThrow(new ResourceNotFoundException());

        // When
        controller.findById(-1L);

        // Then
    }

    @Test
    public void shouldFindTaskById() {
        // Given
        Task task = aTask().id(1L).title("taskTitle1").description("description1").deadLine(now()).build();
        when(taskService.findById(task.getId())).thenReturn(task);

        // When
        Task actualTask = controller.findById(task.getId());

        // Then
        Assertions.assertThat(actualTask).isEqualTo(task);
    }

    @Test
    public void shouldFindAllTasks() {
        // Given
        Task task1 = aTask().id(1L).title("taskTitle1").description("description1").deadLine(now()).build();
        Task task2 = aTask().id(2L).title("taskTitle2").description("description2").deadLine(now()).build();
        Task task3 = aTask().id(3L).title("taskTitle3").description("description3").deadLine(now()).build();
        List<Task> tasks = Arrays.asList(task1, task2, task3);
        when(taskService.findAll()).thenReturn(tasks);

        // When
        List<Task> actualTasks = controller.findAll();

        // Then
        Assertions.assertThat(actualTasks).hasSameSizeAs(tasks).hasSameElementsAs(tasks);
    }

    @Test
    public void shouldFindTaskByTitle() {
        // Given
        Task task = aTask().id(1L).title("taskTitle1").description("description1").deadLine(now()).build();
        when(taskService.findByTitle(task.getTitle())).thenReturn(Collections.singletonList(task));

        // When
        List<Task> actualTasks = controller.findByTitle(task.getTitle());

        // Then
        Assertions.assertThat(actualTasks).hasSize(1).containsOnly(task);
    }

    @Test
    public void shouldFindAllTaskByTitle() {
        // Given
        Task task1 = aTask().id(1L).title("taskTitle").description("description1").deadLine(now()).build();
        Task task2 = aTask().id(2L).title("taskTitle2").description("description2").deadLine(now()).build();
        Task task3 = aTask().id(3L).title("taskTitle").description("description3").deadLine(now()).build();
        List<Task> tasks = Arrays.asList(task1, task2, task3);
        when(taskService.findByTitle(task1.getTitle())).thenReturn(tasks);

        // When
        List<Task> actualTasks = controller.findByTitle(task1.getTitle());

        // Then
        Assertions.assertThat(actualTasks).hasSameSizeAs(tasks).hasSameElementsAs(tasks);
    }

    @Test
    public void shouldDeleteTask() {
        // Given
        Task task = aTask().id(1L).title("taskTitle1").description("description1").deadLine(now()).build();

        // When
        controller.delete(task.getId());

        // Then
        verify(taskService, times(1)).delete(task.getId());
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void shouldThrowEmptyResultDataAccessExceptionWhenDeleteUnknownTask() {
        // Given
        doThrow(new EmptyResultDataAccessException("No user with id exists!", 1)).when(taskService).delete(anyLong());

        // When
        controller.delete(-1L);

        // Then
    }

    @Test
    public void shouldAssignTaskToUser() throws Exception {
        // Given
        // When
        controller.assignTaskToUser(1L, 2L);

        // Then
        verify(taskService, times(1)).assignTaskToUser(1L, 2L);
    }

    @Test(expected = TaskNotAssignableException.class)
    public void shouldThrowTaskNotAssignableExceptionWhenAssignOverdueTaskToUser() throws Exception {
        // Given
        when(taskService.assignTaskToUser(1L, 2L)).thenThrow(new TaskNotAssignableException("task is not assignable (done or overdue)"));

        // When
        controller.assignTaskToUser(1L, 2L);

        // Then
    }
}
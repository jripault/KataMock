package org.codingdojo.controller;

import org.assertj.core.api.Assertions;
import org.codingdojo.domain.Task;
import org.codingdojo.exception.ResourceNotFoundException;
import org.codingdojo.service.impl.TaskServiceImpl;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TaskControllerTest {

    @InjectMocks
    TaskController controller;

    @Mock
    private TaskServiceImpl taskService;

    @Test
    public void shouldCreateValidTask() throws Exception {
        // Given
        Task task = new Task().builder().title("taskTitle1").description("description1").deadLine(new Date()).build();

        // When
        controller.create(task);

        // Then
        verify(taskService, times(1)).save(task);
    }

    @Test(expected = ConstraintViolationException.class)
    public void shouldThrowConstraintViolationExceptionIfTaskIsInvalid() throws Exception {
        // Given
        when(taskService.save(any(Task.class))).thenThrow(ConstraintViolationException.class);

        // When
        controller.create(new Task());

        // Then
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundExceptionIfTaskNotExists() throws Exception {
        // Given
        when(taskService.findById(-1L)).thenReturn(null);

        // When
        controller.findById(-1L);

        // Then
    }

    @Test
    public void shouldFindTaskById() throws Exception {
        // Given
        Task task = new Task().builder().id(1L).title("taskTitle1").description("description1").deadLine(new Date()).build();
        when(taskService.findById(task.getId())).thenReturn(task);

        // When
        Task actualTask = controller.findById(task.getId());

        // Then
        Assertions.assertThat(actualTask).isEqualTo(task);
    }

    @Test
    public void shouldFindAllTasks() throws Exception {
        // Given
        Task task1 = new Task().builder().id(1L).title("taskTitle1").description("description1").deadLine(new Date()).build();
        Task task2 = new Task().builder().id(2L).title("taskTitle1").description("description2").deadLine(new Date()).build();
        Task task3 = new Task().builder().id(3L).title("taskTitle1").description("description3").deadLine(new Date()).build();

        List<Task> tasks = Arrays.asList(task1, task2, task3);
        when(taskService.findAll()).thenReturn(tasks);

        // When
        List<Task> actualTasks = controller.findAll();

        // Then
        Assertions.assertThat(actualTasks).hasSameSizeAs(tasks).hasSameElementsAs(tasks);
    }

    @Test
    public void shouldFindTaskByTitle() throws Exception {
        // Given
        Task task = new Task().builder().id(1L).title("taskTitle1").description("description1").deadLine(new Date()).build();
        List<Task> tasks = Arrays.asList(task);
        when(taskService.findByTitle(task.getTitle())).thenReturn(Arrays.asList(task));

        // When
        List<Task> actualTasks = controller.findByTitle(task.getTitle());

        // Then
        Assertions.assertThat(actualTasks).hasSameSizeAs(tasks).hasSameElementsAs(tasks);
    }

    @Test
    public void shouldFindAllTaskByName() throws Exception {
        // Given
        Task task1 = new Task().builder().id(1L).title("taskTitle1").description("description1").deadLine(new Date()).build();
        Task task2 = new Task().builder().id(2L).title("taskTitle2").description("description2").deadLine(new Date()).build();
        List<Task> tasks = Arrays.asList(task1, task2);
        when(taskService.findByTitle(task1.getTitle())).thenReturn(tasks);

        // When
        List<Task> actualTasks = controller.findByTitle(task1.getTitle());

        // Then
        Assertions.assertThat(actualTasks).hasSameSizeAs(tasks).hasSameElementsAs(tasks);
    }

    @Test
    public void shouldDeleteTaskIfExist() throws Exception {
        // Given
        Task task = new Task().builder().id(1L).title("taskTitle1").description("description1").deadLine(new Date()).build();

        // When
        controller.delete(task.getId());

        // Then
        verify(taskService, times(1)).delete(task.getId());
    }

    @Test
    public void shouldAssignTaskToUser() throws Exception {
        // Given
        // When
        controller.assignTaskToUser(1L, 2L);

        // Then
        verify(taskService, times(1)).assignTaskToUser(1L, 2L);
    }
}
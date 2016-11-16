package org.codingdojo.service.impl;

import org.codingdojo.domain.Task;
import org.codingdojo.domain.User;
import org.codingdojo.exception.TaskNotAssignableException;
import org.codingdojo.repository.TaskRepository;
import org.codingdojo.service.NotificationService;
import org.codingdojo.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.codingdojo.domain.TaskBuilder.aTask;
import static org.codingdojo.domain.UserBuilder.anUser;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TaskServiceImplTest {

    @InjectMocks
    private TaskServiceImpl taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @Mock
    private NotificationService notificationService;

    @Test
    public void shouldAssignTaskToUser() {
        // Given
        Task task = aTask().id(1L).title("taskTitle").description("description").deadLine(now().plusDays(1)).build();
        when(taskRepository.findOne(1L)).thenReturn(task);

        User user = anUser().id(10L).name("userName").email("user.email@test.org").build();
        when(userService.findById(10L)).thenReturn(user);

        // When
        Task assignedTask = taskService.assignTaskToUser(task.getId(), user.getId());

        // Then
        assertThat(assignedTask.getUser()).isEqualTo(user);
    }

    @Test
    public void shouldAssignTaskToUserAndNotifyHim() {
        // Given
        Task task = aTask().id(1L).title("taskTitle").description("description").deadLine(now().plusDays(1)).build();
        when(taskRepository.findOne(1L)).thenReturn(task);

        User user = anUser().id(10L).name("userName").email("user.email@test.org").build();
        when(userService.findById(10L)).thenReturn(user);

        // When
        Task assignedTask = taskService.assignTaskToUser(task.getId(), user.getId());

        // Then
        assertThat(assignedTask.getUser()).isEqualTo(user);
        verify(notificationService, times(1)).send(user.getEmail(), String.format("The task: '%s' has been assigned to you.", task.getTitle()));
    }

    @Test
    public void shouldAssignTaskFromPreviousUserToNewUserAndNotifyThem() {
        // Given
        User previousUser = anUser().id(10L).name("previousUser").email("previous.user.email@test.org").build();
        Task task = aTask().id(1L).title("taskTitle").description("description").deadLine(now().plusDays(1)).user(previousUser).build();
        when(taskRepository.findOne(1L)).thenReturn(task);

        User user = anUser().id(10L).name("userName").email("user.email@test.org").build();
        when(userService.findById(10L)).thenReturn(user);

        // When
        Task assignedTask = taskService.assignTaskToUser(task.getId(), user.getId());

        // Then
        assertThat(assignedTask.getUser()).isEqualTo(user);
        verify(notificationService, times(1)).send(user.getEmail(), String.format("The task: '%s' has been assigned to you.", task.getTitle()));
        verify(notificationService, times(1)).send(previousUser.getEmail(), String.format("The task: '%s' has been assigned to somebody else.", task.getTitle()));
    }

    @Test(expected = TaskNotAssignableException.class)
    public void shouldNotAssignOverdueTaskToUser() {
        // Given
        Task task = aTask().id(1L).title("taskTitle").description("description").deadLine(now().minusDays(1)).build();
        when(taskRepository.findOne(1L)).thenReturn(task);

        // When
        taskService.assignTaskToUser(task.getId(), 10L);

        // Then
    }

    @Test(expected = TaskNotAssignableException.class)
    public void shouldNotAssignDoneTaskToUser() {
        // Given
        Task task = aTask().id(1L).title("taskTitle").description("description").deadLine(now().plusDays(1)).done(true).build();
        when(taskRepository.findOne(1L)).thenReturn(task);

        // When
        taskService.assignTaskToUser(task.getId(), 10L);

        // Then
    }

    @Test
    public void shouldAssignMultipleTasksToUserAndNotifyHim() {
        // Given
        List<Long> taskIds = Stream.of(1L, 2L, 3L).collect(Collectors.toList());
        TaskServiceImpl taskService = spy(new TaskServiceImpl(taskRepository, userService, notificationService));
        doReturn(new Task()).when(taskService).assignTaskToUser(anyLong(), anyLong());

        // When
        taskService.assignTasksToUser(taskIds, 10L);

        // Then
        verify(taskService, times(1)).assignTaskToUser(1L, 10L);
        verify(taskService, times(1)).assignTaskToUser(2L, 10L);
        verify(taskService, times(1)).assignTaskToUser(3L, 10L);
    }
}
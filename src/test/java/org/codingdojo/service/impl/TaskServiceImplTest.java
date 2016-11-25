package org.codingdojo.service.impl;

import org.codingdojo.domain.Task;
import org.codingdojo.domain.User;
import org.codingdojo.repository.TaskRepository;
import org.codingdojo.service.NotificationService;
import org.codingdojo.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.codingdojo.domain.TaskBuilder.aTask;
import static org.codingdojo.domain.UserBuilder.anUser;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

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

        // When

        // Then
        fail("Not yet implemented!");
    }

    @Test
    public void shouldAssignTaskFromPreviousUserToNewUserAndNotifyThem() {
        // Given

        // When

        // Then
        fail("Not yet implemented!");
    }

    @Test
    public void shouldNotAssignOverdueTaskToUser() {
        // Given

        // When

        // Then
        fail("Not yet implemented!");
    }

    @Test
    public void shouldNotAssignDoneTaskToUser() {
        // Given

        // When

        // Then
        fail("Not yet implemented!");
    }

    @Test
    public void shouldAssignMultipleTasksToUserAndNotifyHim() {
        // Given

        // When

        // Then
        fail("Not yet implemented!");
    }
}
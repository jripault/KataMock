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
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * User: JRI <julien.ripault@atos.net>
 * Date: 29/03/2016
 */
@RunWith(MockitoJUnitRunner.class)
public class TaskServiceImplTest {

    @InjectMocks
    TaskServiceImpl taskService;

    @Mock
    TaskRepository taskRepository;

    @Mock
    UserService userService;

    @Mock
    NotificationService notificationService;

    @Test
    public void shouldAssignTaskToUser() throws Exception {
        // GIVEN
        Task task = new Task().builder().id(1L).title("taskTitle").description("description").deadLine(now()).build();
        when(taskRepository.findOne(1L)).thenReturn(task);

        User user = new User().builder().id(10L).name("userName").email("user.email@test.org").build();
        when(userService.findById(10L)).thenReturn(user);

        // WHEN
        Task assignedTask = taskService.assignTaskToUser(task.getId(), user.getId());

        // THEN
        assertThat(assignedTask.getUser()).isEqualTo(user);
        verify(notificationService, times(1)).send(eq(user.getEmail()), anyString());
    }

    @Test
    public void shouldAssignTaskFromPreviousUserToNewUser() throws Exception {
        // GIVEN
        User previousUser = new User().builder().id(10L).name("previousUser").email("previous.user.email@test.org").build();
        Task task = new Task().builder().id(1L).title("taskTitle").description("description").deadLine(now()).user(previousUser).build();
        when(taskRepository.findOne(1L)).thenReturn(task);

        User user = new User().builder().id(10L).name("userName").email("user.email@test.org").build();
        when(userService.findById(10L)).thenReturn(user);

        // WHEN
        Task assignedTask = taskService.assignTaskToUser(task.getId(), user.getId());

        // THEN
        assertThat(assignedTask.getUser()).isEqualTo(user);
        verify(notificationService, times(1)).send(eq(user.getEmail()), anyString());
        verify(notificationService, times(1)).send(eq(previousUser.getEmail()), anyString());
    }
}
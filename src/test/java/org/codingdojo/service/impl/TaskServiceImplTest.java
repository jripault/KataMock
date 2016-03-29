package org.codingdojo.service.impl;

import org.codingdojo.domain.Task;
import org.codingdojo.domain.TaskBuilder;
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
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * User: JRI <julien.ripault@atos.net>
 * Date: 29/03/2016
 */
//@PrepareForTest(LocalDateTime.class)
//@RunWith(PowerMockRunner.class)
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
        Task task = TaskBuilder.aTask().id(1L).title("taskTitle").description("description").deadLine(now().plusDays(1)).build();
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
        Task task = TaskBuilder.aTask().id(1L).title("taskTitle").description("description").deadLine(now().plusDays(1)).user(previousUser).build();
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

    @Test(expected = TaskNotAssignableException.class)
    public void shouldNotAssignTaskBecauseOverdue() throws Exception {
        // GIVEN
        Task task = TaskBuilder.aTask().id(1L).title("taskTitle").description("description").deadLine(now().minusDays(1)).build();
        when(taskRepository.findOne(1L)).thenReturn(task);

        // WHEN
        Task assignedTask = taskService.assignTaskToUser(task.getId(), 10L);

        // THEN
    }

    @Test
    public void shouldAssignMultipleTasks() throws Exception {
        // GIVEN
        List<Long> taskIds = Stream.of(1L, 2L, 3L).collect(Collectors.toList());
        TaskServiceImpl taskService = spy(new TaskServiceImpl(taskRepository, userService, notificationService));
        doReturn(new Task()).when(taskService).findById(anyLong());
        doReturn(new Task()).when(taskService).assignTaskToUser(anyLong(), anyLong());

        // WHEN
        taskService.assignTasksToUser(taskIds, 10L);

        // THEN
        verify(taskService, times(1)).assignTaskToUser(1L, 10L);
        verify(taskService, times(1)).assignTaskToUser(2L, 10L);
        verify(taskService, times(1)).assignTaskToUser(3L, 10L);
    }

   /* @Test(expected = NoSundayRuleException.class)
    public void shouldNotCreateTaskOnSundays() throws Exception {
        // GIVEN
        LocalDateTime nextSunday = now().with(next(DayOfWeek.SUNDAY));


        PowerMockito.mockStatic(LocalDateTime.class);
        when(LocalDateTime.now()).thenReturn(nextSunday);

        Task task = TaskBuilder.aTask().id(1L).title("taskTitle").description("description").build();
        when(taskRepository.findOne(1L)).thenReturn(task);

        // WHEN
        System.out.println(nextSunday);
        System.out.println(now());
        Task assignedTask = taskService.save(task);

        // THEN
    }*/
}
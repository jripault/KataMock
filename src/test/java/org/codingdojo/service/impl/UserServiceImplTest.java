package org.codingdojo.service.impl;

import org.codingdojo.domain.Role;
import org.codingdojo.domain.Task;
import org.codingdojo.domain.User;
import org.codingdojo.repository.UserRepository;
import org.codingdojo.service.NotificationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * User: JRI <julien.ripault@atos.net>
 * Date: 29/03/2016
 */
@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    @Mock
    NotificationService notificationService;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    public void shouldNotifyOnUserDeletion() throws Exception {
        // GIVEN
        User user = new User().builder().build();
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task().builder().done(true).build());
        tasks.add(new Task().builder().done(false).build());
        tasks.add(new Task().builder().done(false).build());
        user.setTasks(tasks);
        User admin = new User().builder().email("admin@codingdojo.org").build();

        when(userRepository.findOne(10L)).thenReturn(user);
        when(userRepository.findByRole(Role.ADMIN)).thenReturn(admin);

        // WHEN
        userService.delete(10L);

        // THEN
        verify(notificationService, times(2)).send(eq(admin.getEmail()), anyString());

    }
}
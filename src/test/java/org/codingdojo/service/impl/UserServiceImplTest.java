package org.codingdojo.service.impl;

import org.codingdojo.domain.Role;
import org.codingdojo.domain.User;
import org.codingdojo.repository.UserRepository;
import org.codingdojo.service.NotificationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.codingdojo.domain.TaskBuilder.aTask;
import static org.codingdojo.domain.UserBuilder.anUser;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserRepository userRepository;


    @Test
    public void shouldNotifyOnUserDeletion() throws Exception {
        // GIVEN
        User admin1 = anUser().email("admin1@codingdojo.org").build();
        User admin2 = anUser().email("admin2@codingdojo.org").build();

        User user = anUser().tasks(Arrays.asList(aTask().done(true).build(), aTask().done(false).build(), aTask().done(false).build())).build();

        when(userRepository.findOne(10L)).thenReturn(user);
        when(userRepository.findByRole(Role.ADMIN)).thenReturn(Arrays.asList(admin1, admin2));

        // WHEN
        userService.delete(10L);

        // THEN
        verify(notificationService, times(2)).send(eq(admin1.getEmail()), anyString());
        verify(notificationService, times(2)).send(eq(admin2.getEmail()), anyString());
    }
}
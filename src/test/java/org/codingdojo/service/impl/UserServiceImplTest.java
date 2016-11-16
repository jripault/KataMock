package org.codingdojo.service.impl;

import org.codingdojo.domain.User;
import org.codingdojo.repository.UserRepository;
import org.codingdojo.service.NotificationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.codingdojo.domain.Role.ADMIN;
import static org.codingdojo.domain.TaskBuilder.aTask;
import static org.codingdojo.domain.UserBuilder.anUser;
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
    public void shouldDeleteUserAndNotifyAdmin() throws Exception {
        // Given
        User admin1 = anUser().email("admin1@codingdojo.org").role(ADMIN).build();
        User admin2 = anUser().email("admin2@codingdojo.org").role(ADMIN).build();

        User user = anUser().tasks(Arrays.asList(aTask().done(true).build(), aTask().done(false).build(), aTask().done(false).build())).build();

        when(userRepository.findOne(10L)).thenReturn(user);
        when(userRepository.findByRole(ADMIN)).thenReturn(Arrays.asList(admin1, admin2));

        // When
        userService.delete(10L);

        // Then
        verify(notificationService, times(2)).send(eq(admin1.getEmail()), anyString());
        verify(notificationService, times(2)).send(eq(admin2.getEmail()), anyString());
    }
}
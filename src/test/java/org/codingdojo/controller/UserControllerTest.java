package org.codingdojo.controller;

import org.assertj.core.api.Assertions;
import org.codingdojo.domain.User;
import org.codingdojo.exception.ResourceNotFoundException;
import org.codingdojo.repository.UserRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    @InjectMocks
    UserController controller;

    @Mock
    private UserRepository userRepository;

    @Test
    public void shouldCreateValidUser() throws Exception {
        // Given
        User user = new User().builder().name("userName").email("user.email@test.org").build();

        // When
        controller.create(user);

        // Then
        verify(userRepository, times(1)).save(user);
    }

    @Test(expected = ConstraintViolationException.class)
    public void shouldThrowConstraintViolationExceptionIfUserIsInvalid() throws Exception {
        // Given
        when(userRepository.save(any(User.class))).thenThrow(ConstraintViolationException.class);

        // When
        controller.create(new User());

        // Then
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundExceptionIfUserNotExists() throws Exception {
        // Given
        when(userRepository.findOne(-1L)).thenReturn(null);

        // When
        controller.findById(-1L);

        // Then
    }

    @Test
    public void shouldFindUserById() throws Exception {
        // Given
        User user = new User().builder().id(1L).name("userName").email("user.email@test.org").build();
        when(userRepository.findOne(user.getId())).thenReturn(user);

        // When
        User actualUser = controller.findById(user.getId());

        // Then
        Assertions.assertThat(actualUser).isEqualTo(user);
    }

    @Test
    public void shouldFindAllUsers() throws Exception {
        // Given
        User user1 = new User().builder().id(1L).name("userName1").email("user.email1@test.org").build();
        User user2 = new User().builder().id(2L).name("userName2").email("user.email2@test.org").build();
        User user3 = new User().builder().id(3L).name("userName3").email("user.email3@test.org").build();
        List<User> users = Arrays.asList(user1, user2, user3);
        when(userRepository.findAll()).thenReturn(users);

        // When
        List<User> actualUsers = controller.findAll();

        // Then
        Assertions.assertThat(actualUsers).hasSameSizeAs(users).hasSameElementsAs(users);
    }

    @Test
    public void shouldFindUserByName() throws Exception {
        // Given
        User user = new User().builder().id(1L).name("userName1").email("user.email1@test.org").build();
        List<User> users = Arrays.asList(user);
        when(userRepository.findByName(user.getName())).thenReturn(Arrays.asList(user));

        // When
        List<User> actualUsers = controller.findByName(user.getName());

        // Then
        Assertions.assertThat(actualUsers).hasSameSizeAs(users).hasSameElementsAs(users);
    }

    @Test
    public void shouldFindAllUserByName() throws Exception {
        // Given
        User user1 = new User().builder().id(1L).name("userName").email("user.email1@test.org").build();
        User user2 = new User().builder().id(2L).name("userName").email("user.email2@test.org").build();
        List<User> users = Arrays.asList(user1, user2);
        when(userRepository.findByName(user1.getName())).thenReturn(users);

        // When
        List<User> actualUsers = controller.findByName(user1.getName());

        // Then
        Assertions.assertThat(actualUsers).hasSameSizeAs(users).hasSameElementsAs(users);
    }

    @Test
    public void shouldDeleteUserIfExist() throws Exception {
        // Given
        User user = new User().builder().id(1L).name("userName").email("user.email@test.org").build();

        // When
        controller.delete(user.getId());

        // Then
        verify(userRepository, times(1)).delete(user.getId());
    }
}
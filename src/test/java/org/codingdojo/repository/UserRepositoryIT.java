package org.codingdojo.repository;

import org.codingdojo.TasksManagementApplication;
import org.codingdojo.domain.User;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TasksManagementApplication.class)
public class UserRepositoryIT {

    @Autowired
    private UserRepository repository;

    @After
    public void tearDown() {
        repository.deleteAll();
    }

    @Test
    public void shouldSaveValidUser() throws InterruptedException {
        // Given
        User user = new User().builder().name("userName1").email("user.email1@test.org").build();

        // When
        repository.save(user);

        // Then
        assertThat(repository.exists(user.getId())).isTrue();
    }

    @Test(expected = ConstraintViolationException.class)
    public void shouldThrowConstraintViolationExceptionIfUserIsInvalidNoMake() {
        // Given
        User user = new User().builder().name("userName1").email("user.email1@test.org").build();

        // When
        repository.save(user);

        // Then
    }

    @Test(expected = ConstraintViolationException.class)
    public void shouldThrowConstraintViolationExceptionIfUserIsInvalidNoModel() {
        // Given
        User user = new User().builder().name("userName1").email("user.email1@test.org").build();

        // When
        repository.save(user);

        // Then
    }

    @Test
    public void shouldFindAllUsers() {
        // Given
        User user1 = new User().builder().name("userName1").email("user.email1@test.org").build();
        User user2 = new User().builder().name("userName2").email("user.email2@test.org").build();
        User user3 = new User().builder().name("userName3").email("user.email3@test.org").build();
        List<User> users = repository.save(Arrays.asList(user1, user2, user3));
        assertThat(repository.count()).isEqualTo(users.size());

        // When
        List<User> all = repository.findAll();

        // Then
        assertThat(all).hasSize(users.size());
    }

    @Test
    public void shouldFindUserById() {
        // Given
        User user1 = new User().builder().name("userName1").email("user.email1@test.org").build();
        User user2 = new User().builder().name("userName2").email("user.email2@test.org").build();
        User user3 = new User().builder().name("userName3").email("user.email3@test.org").build();
        List<User> users = repository.save(Arrays.asList(user1, user2, user3));
        assertThat(repository.count()).isEqualTo(users.size());

        // When
        User collectedUser = repository.findOne(user1.getId());

        // Then
        assertThat(collectedUser).isSameAs(user1);
    }

    @Test
    public void shouldFindUsersByName() {
        // Given
        User user1 = new User().builder().name("userName1").email("user.email1@test.org").build();
        User user2 = new User().builder().name("userName2").email("user.email2@test.org").build();
        User user3 = new User().builder().name("userName3").email("user.email3@test.org").build();
        List<User> users = repository.save(Arrays.asList(user1, user2, user3));
        assertThat(repository.count()).isEqualTo(users.size());

        // When
        List<User> collectedUsers = repository.findByName(user1.getName());

        // Then
        assertThat(collectedUsers).hasSize(2).containsExactly(user1, user2);
    }

    @Test
    public void shouldFindAllUsersByName() {
        // Given
        User user1 = new User().builder().name("userName1").email("user.email1@test.org").build();
        User user2 = new User().builder().name("userName2").email("user.email2@test.org").build();
        User user3 = new User().builder().name("userName3").email("user.email3@test.org").build();
        List<User> users = repository.save(Arrays.asList(user1, user2, user3));
        assertThat(repository.count()).isEqualTo(users.size());

        // When
        List<User> collectedUsers = repository.findByName(user1.getName());

        // Then
        assertThat(collectedUsers).hasSize(2).containsExactly(user1, user2);
    }
}
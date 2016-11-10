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

import static org.assertj.core.api.Assertions.assertThat;
import static org.codingdojo.domain.UserBuilder.anUser;

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
        User user = anUser().name("userName1").email("user.email1@test.org").build();

        // When
        repository.save(user);

        // Then
        assertThat(repository.exists(user.getId())).isTrue();
    }



    @Test(expected = ConstraintViolationException.class)
    public void shouldThrowConstraintViolationExceptionIfUserIsInvalid() {
        // Given
        User user = anUser().name("userName1").email("user.email1test.org").build();

        // When
        repository.save(user);

        // Then
    }
}
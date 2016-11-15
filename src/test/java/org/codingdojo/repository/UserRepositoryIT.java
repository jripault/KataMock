package org.codingdojo.repository;

import org.codingdojo.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.codingdojo.domain.UserBuilder.anUser;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryIT {

    @Autowired
    private UserRepository repository;

    @Test
    public void shouldCreateValidUser() {
        // Given
        User user = anUser().name("userName1").email("user.email1@test.org").build();

        // When
        repository.save(user);

        // Then
        assertThat(repository.exists(user.getId())).isTrue();
    }

    @Test(expected = ConstraintViolationException.class)
    public void shouldThrowConstraintViolationExceptionWhenCreateInvalidUser() {
        // Given
        User user = anUser().name("userName1").email("invalid_email.org").build();

        // When
        repository.save(user);

        // Then
    }
}
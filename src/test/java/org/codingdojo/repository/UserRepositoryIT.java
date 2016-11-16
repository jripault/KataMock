package org.codingdojo.repository;

import org.codingdojo.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.codingdojo.domain.Role.ADMIN;
import static org.codingdojo.domain.Role.USER;
import static org.codingdojo.domain.UserBuilder.anUser;

@RunWith(SpringRunner.class)
@DataJpaTest(showSql = false)
public class UserRepositoryIT {

    @Autowired
    private UserRepository repository;

    @Autowired
    private TestEntityManager entityManager;

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

    @Test
    public void shouldFindUserByName() {
        // Given
        User user1 = anUser().name("userName1").email("user.email1@test.org").build();
        entityManager.persist(user1);
        User user2 = anUser().name("userName2").email("user.email2@test.org").build();
        entityManager.persist(user2);
        User user3 = anUser().name("userName3").email("user.email3@test.org").build();
        entityManager.persist(user3);

        // When
        List<User> actualUsers = repository.findByName(user1.getName());

        // Then
        assertThat(actualUsers).hasSize(1).containsOnly(user1);
    }

    @Test
    public void shouldFindAllUsersWithSameName() {
        // Given
        User user1 = anUser().name("userName").email("user.email1@test.org").build();
        entityManager.persist(user1);
        User user2 = anUser().name("userName2").email("user.email2@test.org").build();
        entityManager.persist(user2);
        User user3 = anUser().name("userName").email("user.email3@test.org").build();
        entityManager.persist(user3);

        // When
        List<User> actualUsers = repository.findByName(user1.getName());

        // Then
        assertThat(actualUsers).hasSize(2).containsOnly(user1, user3);
    }

    @Test
    public void shouldFindUserByRole() {
        // Given
        User user1 = anUser().name("userName1").email("user.email1@test.org").role(ADMIN).build();
        entityManager.persist(user1);
        User user2 = anUser().name("userName2").email("user.email2@test.org").role(USER).build();
        entityManager.persist(user2);
        User user3 = anUser().name("userName").email("user.email3@test.org").role(ADMIN).build();
        entityManager.persist(user3);

        // When
        List<User> actualUsers = repository.findByRole(USER);

        // Then
        assertThat(actualUsers).hasSize(1).containsOnly(user2);
    }

    @Test
    public void shouldFindAllUsersWithSameRole() {
        // Given
        User user1 = anUser().name("userName1").email("user.email1@test.org").role(USER).build();
        entityManager.persist(user1);
        User user2 = anUser().name("userName2").email("user.email2@test.org").role(ADMIN).build();
        entityManager.persist(user2);
        User user3 = anUser().name("userName").email("user.email3@test.org").role(USER).build();
        entityManager.persist(user3);

        // When
        List<User> actualUsers = repository.findByRole(user1.getRole());

        // Then
        assertThat(actualUsers).hasSize(2).containsOnly(user1, user3);
    }
}
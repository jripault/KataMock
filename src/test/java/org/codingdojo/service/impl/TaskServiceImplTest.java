package org.codingdojo.service.impl;

import org.codingdojo.domain.Task;
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
@RunWith(MockitoJUnitRunner.class)
public class TaskServiceImplTest {


    @Test
    public void shouldAssignTaskToUser() throws Exception {
        // GIVEN

        // WHEN

        // THEN
    }

    @Test
    public void shouldAssignTaskFromPreviousUserToNewUser() throws Exception {
        // GIVEN

        // WHEN

        // THEN
    }

    @Test
    public void shouldNotAssignTaskBecauseOverdue() throws Exception {
        // GIVEN

        // WHEN

        // THEN
    }

    @Test
    public void shouldAssignMultipleTasks() throws Exception {
        // GIVEN

        // WHEN

        // THEN
    }

}
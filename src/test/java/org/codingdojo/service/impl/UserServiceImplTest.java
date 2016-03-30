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

    @Test
    public void shouldNotifyOnUserDeletion() throws Exception {
        // GIVEN

        // WHEN

        // THEN

    }
}
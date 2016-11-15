package org.codingdojo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.apache.commons.lang3.StringUtils;
import org.codingdojo.domain.User;
import org.codingdojo.repository.TaskRepository;
import org.codingdojo.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.codingdojo.domain.Role.ADMIN;
import static org.codingdojo.domain.TaskBuilder.aTask;
import static org.codingdojo.domain.UserBuilder.anUser;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class UserControllerIT {

    private static final String BASE_URI = "/api/user/";

    @Rule
    public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP);

    @Autowired
    private WebApplicationContext context;

    @Autowired
    //Used to delete/add the data added for tests (directly invoke the APIs interacting with the DB)
    private UserRepository repository;

    @Autowired
    //Used to delete/add the data added for tests (directly invoke the APIs interacting with the DB)
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @After
    public void tearDown() {
        repository.deleteAll();
        taskRepository.deleteAll();
    }

    @Test
    public void shouldCreateValidUser() throws Exception {
        // Given
        User user = anUser().name("userName1").email("user.email1@test.org").build();

        // When
        ResultActions response = mockMvc.perform(post(BASE_URI)
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isCreated());
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    public void shouldReturn400WhenCreateInvalidUser() throws Exception {
        // Given
        User user = anUser().name("userName1").email("invalid_email.org").build();

        // When
        ResultActions response = mockMvc.perform(post(BASE_URI)
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isBadRequest());
        assertThat(repository.count()).isEqualTo(0);
    }

    @Test
    public void shouldFindAllUsers() throws Exception {
        // Given
        User user1 = anUser().name("userName1").email("user.email1@test.org").build();
        User user2 = anUser().name("userName2").email("user.email2@test.org").build();
        User user3 = anUser().name("userName3").email("user.email3@test.org").build();
        List<User> users = repository.save(Arrays.asList(user1, user2, user3));
        assertThat(repository.count()).isEqualTo(users.size());

        // When
        ResultActions response = mockMvc.perform(get(BASE_URI));

        // Then
        response.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(users)));
    }

    @Test
    public void shouldFindUserById() throws Exception {
        // Given
        User user1 = anUser().name("userName1").email("user.email1@test.org").build();
        User user2 = anUser().name("userName2").email("user.email2@test.org").build();
        User user3 = anUser().name("userName3").email("user.email3@test.org").build();
        List<User> users = repository.save(Arrays.asList(user1, user2, user3));
        assertThat(repository.count()).isEqualTo(users.size());

        // When
        ResultActions response = mockMvc.perform(get(BASE_URI + "{id}", user1.getId()));

        // Then
        response.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user1)));
    }

    @Test
    public void shouldReturn404WhenFindUnknownUser() throws Exception {
        // Given
        // When
        ResultActions response = mockMvc.perform(get(BASE_URI + "{id}", -1));

        // Then
        response.andExpect(status().isNotFound());
    }

    @Test
    public void shouldFindUserByName() throws Exception {
        // Given
        User user1 = anUser().name("userName1").email("user.email1@test.org").build();
        User user2 = anUser().name("userName2").email("user.email2@test.org").build();
        User user3 = anUser().name("userName3").email("user.email3@test.org").build();
        List<User> users = repository.save(Arrays.asList(user1, user2, user3));
        assertThat(repository.count()).isEqualTo(users.size());

        // When
        ResultActions response = mockMvc.perform(get(BASE_URI + "/name/{name}", user1.getName()));

        // Then
        response.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.singletonList(user1))));
    }

    @Test
    public void shouldFindAllUsersByName() throws Exception {
        // Given
        User user1 = anUser().name("userName").email("user.email1@test.org").build();
        User user2 = anUser().name("userName2").email("user.email2@test.org").build();
        User user3 = anUser().name("userName").email("user.email3@test.org").build();
        List<User> users = repository.save(Arrays.asList(user1, user2, user3));
        assertThat(repository.count()).isEqualTo(users.size());

        // When
        ResultActions response = mockMvc.perform(get(BASE_URI + "/name/{name}", user1.getName()));

        // Then
        response.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(user1, user3))));
    }

    @Test
    public void shouldDeleteUser() throws Exception {
        // Given
        User user1 = anUser().name("userName1").email("user.email1@test.org").build();
        User user2 = anUser().name("userName2").email("user.email2@test.org").build();
        User user3 = anUser().name("userName3").email("user.email3@test.org").build();
        List<User> users = repository.save(Arrays.asList(user1, user2, user3));
        assertThat(repository.count()).isEqualTo(users.size());

        // When
        ResultActions response = mockMvc.perform(delete(BASE_URI + "/{id}", user1.getId()));

        // Then
        response.andExpect(status().isNoContent())
                .andExpect(content().string(StringUtils.EMPTY));
        assertThat(repository.count()).isEqualTo(users.size() - 1);
    }

    @Test
    public void shouldDeleteUserAndNotifyAdmin() throws Exception {
        // Given
        User user1 = anUser().name("userName1").email("user.email1@test.org").tasks(Arrays.asList(
                aTask().title("taskTitle1").description("description1").deadLine(now().plusMinutes(5)).build(),
                aTask().title("taskTitle2").description("description2").deadLine(now().plusMinutes(5)).build()
        )).build();
        User admin = anUser().name("userName3").email("user.email3@test.org").role(ADMIN).build();
        List<User> users = repository.save(Arrays.asList(user1, admin));
        assertThat(repository.count()).isEqualTo(users.size());

        // When
        ResultActions response = mockMvc.perform(delete(BASE_URI + "/{id}", user1.getId()));

        // Then
        response.andExpect(status().isNoContent())
                .andExpect(content().string(StringUtils.EMPTY));
        assertThat(repository.count()).isEqualTo(users.size() - 1);
    }


    @Test
    public void shouldReturn400WhenDeleteUnknownUser() throws Exception {
        // Given
        // When
        ResultActions response = mockMvc.perform(delete(BASE_URI + "/{id}", -1L));

        // Then
        response.andExpect(status().isNotFound());
    }
}
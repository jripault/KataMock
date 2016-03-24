package org.codingdojo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.codingdojo.TasksManagementApplication;
import org.codingdojo.domain.User;
import org.codingdojo.repository.UserRepository;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(DataProviderRunner.class)
@SpringApplicationConfiguration(TasksManagementApplication.class)
@WebAppConfiguration
public class UserControllerIT {

    //See http://docs.spring.io/spring/docs/4.2.x/spring-framework-reference/html/integration-testing.html#testcontext-junit4-rules
    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    private static final String BASE_URI = "/api/user/";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    //Used to delete/add the data added for tests (directly invoke the APIs interacting with the DB)
    private UserRepository repository;

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
    }

    @DataProvider
    public static Object[][] invalidUsers() {
        return new Object[][]{
                {new User().builder().name("").email("user.email@test.org").build()},
                {new User().builder().name("userName").email("user.emailtest.org").build()},
        };
    }

    @Test
    public void shouldCreateValidUser() throws Exception {
        // Given
        User user = new User().builder().name("userName").email("user.email@test.org").build();

        // When
        ResultActions response = mockMvc.perform(post(BASE_URI)
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isCreated());
    }

    @Test
    @UseDataProvider("invalidUsers")
    public void shouldReturn400IfUserIsInvalid(User user) throws Exception {
        // Given

        // When
        ResultActions response = mockMvc.perform(post(BASE_URI)
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturn404IfUserNotExists() throws Exception {
        // Given
        // When
        ResultActions response = mockMvc.perform(get(BASE_URI + "{id}", -1));

        // Then
        response.andExpect(status().isNotFound());
    }

    @Test
    public void shouldFindUserById() throws Exception {
        // Given
        User user1 = new User().builder().name("userName1").email("user.email1@test.org").build();
        User user2 = new User().builder().name("userName2").email("user.email2@test.org").build();
        User user3 = new User().builder().name("userName3").email("user.email3@test.org").build();
        List<User> users = repository.save(Arrays.asList(user1, user2, user3));
        assertThat(repository.count()).isEqualTo(users.size());

        // When
        ResultActions response = mockMvc.perform(get(BASE_URI + "{id}", user1.getId()));

        // Then
        response.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user1)));
    }

    @Test
    public void shouldFindAllUsers() throws Exception {
        // Given
        User user1 = new User().builder().name("userName1").email("user.email1@test.org").build();
        User user2 = new User().builder().name("userName2").email("user.email2@test.org").build();
        User user3 = new User().builder().name("userName3").email("user.email3@test.org").build();
        List<User> users = repository.save(Arrays.asList(user1, user2, user3));
        assertThat(repository.count()).isEqualTo(users.size());

        // When
        ResultActions response = mockMvc.perform(get(BASE_URI));

        // Then
        response.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(users)));
    }

    @Test
    public void shouldFindUserByName() throws Exception {
        // Given
        User user1 = new User().builder().name("userName1").email("user.email1@test.org").build();
        User user2 = new User().builder().name("userName2").email("user.email2@test.org").build();
        User user3 = new User().builder().name("userName3").email("user.email3@test.org").build();
        List<User> users = repository.save(Arrays.asList(user1, user2, user3));
        assertThat(repository.count()).isEqualTo(users.size());

        // When
        ResultActions response = mockMvc.perform(get(BASE_URI + "name/" + "{name}", "userName1"));

        // Then
        response.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(user1))));
    }

    @Test
    public void shouldFindAllUserByName() throws Exception {
        // Given
        User user1 = new User().builder().name("userName1").email("user.email1@test.org").build();
        User user2 = new User().builder().name("userName2").email("user.email2@test.org").build();
        User user3 = new User().builder().name("userName1").email("user.email3@test.org").build();
        List<User> users = repository.save(Arrays.asList(user1, user2, user3));
        assertThat(repository.count()).isEqualTo(users.size());

        // When
        ResultActions response = mockMvc.perform(get(BASE_URI + "name/" + "{name}", "userName1"));

        // Then
        response.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(user1, user3))));
    }

    @Test
    public void shouldDeleteUserIfExist() throws Exception {
        // Given
        User user = new User().builder().name("userName1").email("user.email1@test.org").build();
        user = repository.save(user);
        assertThat(repository.exists(user.getId())).isTrue();

        // When
        ResultActions response = mockMvc.perform(delete(BASE_URI + "{id}", user.getId()));

        // Then
        response.andExpect(status().isNoContent());
    }
}
package org.codingdojo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.codingdojo.TasksManagementApplication;
import org.codingdojo.domain.Task;
import org.codingdojo.domain.User;
import org.codingdojo.repository.TaskRepository;
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
import java.util.Date;
import java.util.List;

import static java.time.LocalDateTime.now;
import static java.time.ZoneId.systemDefault;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(DataProviderRunner.class)
@SpringApplicationConfiguration(TasksManagementApplication.class)
@WebAppConfiguration
public class TaskControllerIT {

    //See http://docs.spring.io/spring/docs/4.2.x/spring-framework-reference/html/integration-testing.html#testcontext-junit4-rules
    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    private static final String BASE_URI = "/api/task/";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    //Used to delete/add the data added for tests (directly invoke the APIs interacting with the DB)
    private TaskRepository repository;

    @Autowired
    //Used to delete/add the data added for tests (directly invoke the APIs interacting with the DB)
    private UserRepository userRepository;

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
    public static Object[][] invalidTasks() {
        Date now = Date.from(now().atZone(systemDefault()).toInstant());
        Date now5 = Date.from(now().plusMinutes(5).atZone(systemDefault()).toInstant());
        return new Object[][]{
                {new Task().builder().title("").deadLine(now5).build()},
                {new Task().builder().title("taskTitle").deadLine(now).build()}
        };
    }

    @Test
    public void shouldCreateValidTask() throws Exception {
        // Given
        Date now5 = Date.from(now().plusMinutes(5).atZone(systemDefault()).toInstant());
        Task task = new Task().builder().title("taskTitle").description("description").deadLine(now5).build();

        // When
        ResultActions response = mockMvc.perform(post(BASE_URI)
                .content(objectMapper.writeValueAsString(task))
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isCreated());
    }

    @Test
    @UseDataProvider("invalidTasks")
    public void should400IfTaskIsInvalid(Task task) throws Exception {
        // Given

        // When
        ResultActions response = mockMvc.perform(post(BASE_URI)
                .content(objectMapper.writeValueAsString(task))
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturn404IfTaskNotExists() throws Exception {
        // Given
        // When
        ResultActions response = mockMvc.perform(get(BASE_URI + "{id}", -1));

        // Then
        response.andExpect(status().isNotFound());
    }

    @Test
    public void shouldFindTaskById() throws Exception {
        // Given
        Date now5 = Date.from(now().plusMinutes(5).atZone(systemDefault()).toInstant());
        Task task1 = new Task().builder().title("taskTitle1").description("description1").deadLine(now5).build();
        Task task2 = new Task().builder().title("taskTitle2").description("description2").deadLine(now5).build();
        Task task3 = new Task().builder().title("taskTitle3").description("description3").deadLine(now5).build();
        List<Task> tasks = repository.save(Arrays.asList(task1, task2, task3));
        assertThat(repository.count()).isEqualTo(tasks.size());

        // When
        ResultActions response = mockMvc.perform(get(BASE_URI + "{id}", task1.getId()));

        // Then
        response.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(task1)));
    }

    @Test
    public void shouldFindAllTasks() throws Exception {
        // Given
        Date now5 = Date.from(now().plusMinutes(5).atZone(systemDefault()).toInstant());
        Task task1 = new Task().builder().title("taskTitle1").description("description1").deadLine(now5).build();
        Task task2 = new Task().builder().title("taskTitle2").description("description2").deadLine(now5).build();
        Task task3 = new Task().builder().title("taskTitle3").description("description3").deadLine(now5).build();
        List<Task> tasks = repository.save(Arrays.asList(task1, task2, task3));
        assertThat(repository.count()).isEqualTo(tasks.size());

        // When
        ResultActions response = mockMvc.perform(get(BASE_URI));

        // Then
        response.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(tasks)));
    }

    @Test
    public void shouldFindTaskByName() throws Exception {
        // Given
        Date now5 = Date.from(now().plusMinutes(5).atZone(systemDefault()).toInstant());
        Task task1 = new Task().builder().title("taskTitle1").description("description1").deadLine(now5).build();
        Task task2 = new Task().builder().title("taskTitle2").description("description2").deadLine(now5).build();
        Task task3 = new Task().builder().title("taskTitle3").description("description3").deadLine(now5).build();
        List<Task> tasks = repository.save(Arrays.asList(task1, task2, task3));
        assertThat(repository.count()).isEqualTo(tasks.size());

        // When
        ResultActions response = mockMvc.perform(get(BASE_URI + "title/" + "{title}", "taskTitle1"));

        // Then
        response.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(task1))));
    }

    @Test
    public void shouldFindAllTaskByName() throws Exception {
        // Given
        Date now5 = Date.from(now().plusMinutes(5).atZone(systemDefault()).toInstant());
        Task task1 = new Task().builder().title("taskTitle").description("description1").deadLine(now5).build();
        Task task2 = new Task().builder().title("taskTitle2").description("description2").deadLine(now5).build();
        Task task3 = new Task().builder().title("taskTitle").description("description3").deadLine(now5).build();
        List<Task> tasks = repository.save(Arrays.asList(task1, task2, task3));
        assertThat(repository.count()).isEqualTo(tasks.size());

        // When
        ResultActions response = mockMvc.perform(get(BASE_URI + "title/" + "{title}", "taskTitle"));

        // Then
        response.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(task1, task3))));
    }

    @Test
    public void shouldDeleteTaskIfExist() throws Exception {
        // Given
        Date now5 = Date.from(now().plusMinutes(5).atZone(systemDefault()).toInstant());
        Task task = new Task().builder().title("taskTitle1").description("description1").deadLine(now5).build();
        task = repository.save(task);
        assertThat(repository.exists(task.getId())).isTrue();

        // When
        ResultActions response = mockMvc.perform(delete(BASE_URI + "{id}", task.getId()));

        // Then
        response.andExpect(status().isNoContent());
    }

    @Test
    public void shouldAssignTaskToUser() throws Exception {
        // Given
        Date now5 = Date.from(now().plusMinutes(5).atZone(systemDefault()).toInstant());
        Task task = new Task().builder().title("taskTitle1").description("description1").deadLine(now5).build();
        task = repository.save(task);
        assertThat(repository.exists(task.getId())).isTrue();

        User user = new User().builder().name("userName1").email("user.email1@test.org").build();
        user = userRepository.save(user);
        assertThat(userRepository.exists(user.getId())).isTrue();

        // When
        ResultActions response = mockMvc.perform(put(BASE_URI + "{taskId}/user/{userId}", task.getId(), user.getId()));

        // Then
        task.setUser(user);
        response.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(task)));
    }

    @Test
    public void shouldReturn400WhenTryToAssignATaskWhoNotExist() throws Exception {
        // Given
        User user = new User().builder().name("userName1").email("user.email1@test.org").build();
        user = userRepository.save(user);
        assertThat(userRepository.exists(user.getId())).isTrue();

        // When
        ResultActions response = mockMvc.perform(put(BASE_URI + "{taskId}/user/{userId}", -1, user.getId()));

        // Then
        response.andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturn400WhenTryToAssignATaskToAnUserWhoNotExist() throws Exception {
        // Given
        Date now5 = Date.from(now().plusMinutes(5).atZone(systemDefault()).toInstant());
        Task task = new Task().builder().title("taskTitle1").description("description1").deadLine(now5).build();
        task = repository.save(task);
        assertThat(repository.exists(task.getId())).isTrue();

        // When
        ResultActions response = mockMvc.perform(put(BASE_URI + "{taskId}/user/{userId}", task.getId(), -1));

        // Then
        response.andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturn400AndWhenTryToAssignATaskWhoIsDone() throws Exception {
        // Given
        Date now5 = Date.from(now().plusMinutes(5).atZone(systemDefault()).toInstant());
        Task task = new Task().builder().title("taskTitle1").description("description1").deadLine(now5).done(true).build();
        task = repository.save(task);
        assertThat(repository.exists(task.getId())).isTrue();

        User user = new User().builder().name("userName1").email("user.email1@test.org").build();
        user = userRepository.save(user);
        assertThat(userRepository.exists(user.getId())).isTrue();

        // When
        ResultActions response = mockMvc.perform(put(BASE_URI + "{taskId}/user/{userId}", task.getId(), user.getId()));

        // Then
        response.andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturn400AndWhenTryToAssignATaskWhoIsOverdue() throws Exception {
        //TODO
    }
}